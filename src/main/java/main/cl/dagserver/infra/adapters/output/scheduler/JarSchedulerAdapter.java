package main.cl.dagserver.infra.adapters.output.scheduler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.nhl.dflib.DataFrame;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.annotations.Dag;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.confs.ChannelScanner;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;
import main.cl.dagserver.infra.adapters.operators.DummyOperator;
import main.cl.dagserver.infra.adapters.operators.LogsRollupOperator;
import main.cl.dagserver.infra.adapters.operators.RegisterSchedulerOperator;
@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
public class JarSchedulerAdapter implements JarSchedulerOutputPort {
	@Autowired
	private StorageOutputPort storage;
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	@Autowired
	private QuartzConfig quartz;
	@Autowired
	private FileSystemOutputPort fileSystem;
	@Autowired
	private ChannelScanner channels;
	
	private static final String CLASSNAME = "classname";
	private static final String CLASSEXT = ".class";
	
	private List<Path> jars = new ArrayList<>();
	private Map<String,List<Map<String,String>>> classMap = new HashMap<>();
	
	public JarSchedulerAdapter init() throws DomainException {
	    this.classMap = new HashMap<>();
	    Path folderPath = fileSystem.getFolderPath();
	    List<Path> jarFiles = new ArrayList<>();
	    try (Stream<Path> paths = Files.walk(folderPath)) {
	        jarFiles = paths
	        .filter(path -> path.toString().endsWith(".jar"))
            .filter(path -> {
                return filtrarEsDag(path);
            })
	        .collect(Collectors.toList());
	    } catch (IOException e) {
	        log.error("JarSchedulerAdapter init:", e);
	    }

	    for (Path jarFile : jarFiles) {
	        
	    	jars.add(jarFile);
	        classMap.put(jarFile.getFileName().toString(), this.analizeJar(jarFile));
	        quartz.validate(jarFile.getFileName().toString().replace(".jar", ""), this.analizeJarProperties(jarFile));
	    }

	    return this;
	}


	private boolean filtrarEsDag(Path path) {
		try (
				ZipFile zfile = new ZipFile(path.toString());
			) {
			ZipEntry entry = zfile.getEntry(".source");
			if(entry!= null) {
				InputStream zip = zfile.getInputStream(entry);
				BufferedReader reader = new BufferedReader(new InputStreamReader(zip, StandardCharsets.UTF_8));
			    String content = reader.readLine();
				reader.close();
				zip.close();
				return "dagserver-generated-jar".equals(content);	
			} else {
				return false;
			}
		} catch (IOException e) {
		    log.error("Error reading .source file from jar:", e);
		    return false;
		}
	}
	
	
	private Map<String,Properties> analizeJarProperties(Path jarFile){
		Map<String,Properties> props = new HashMap<>();
		try(
				InputStream inputStream = Files.newInputStream(jarFile);
				ZipInputStream zip = new ZipInputStream(inputStream);) {
				ZipEntry ze;
				while ((ze = zip.getNextEntry()) != null) {
					 if (!ze.isDirectory() && ze.getName().endsWith(".properties")) {
					    	var prop = new Properties();
					    	prop.load(fileSystem.loadResourceFromJar(jarFile, ze.getName()));
					    	String[] name = ze.getName().replace(".properties", "").split("/");
					    	props.put(name[name.length-1], prop);
					  }	 
				}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "analizeJarProperties"));
		}
		return props;
	}
	
	public Properties getProperties(Path jarFile) {
		Properties prop = new Properties();
		try(
				InputStream inputStream = Files.newInputStream(jarFile);
				ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.toString()));
				
				) {
			ZipEntry ze;
	        while ((ze = zip.getNextEntry()) != null) {
				 if (!ze.isDirectory() && ze.getName().endsWith("properties")) {
		             prop.load(inputStream);
				 }
			}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "getProperties"));
		}
		return prop;
	}
	private List<Map<String,String>> analizeJar(Path jarFile) {
	    List<Map<String,String>> classNames = new ArrayList<>();
	    try (
	    		ZipFile zfile = new ZipFile(jarFile.toString());
	    		BufferedInputStream buffered = new BufferedInputStream(Files.newInputStream(jarFile))) {
	    	String owner = getOwnerFromZip(zfile);
	    	try (ZipInputStream zip = new ZipInputStream(buffered)) {
	            ZipEntry ze;
	            while ((ze = zip.getNextEntry()) != null) {
	                if (!ze.isDirectory() && ze.getName().endsWith(CLASSEXT)) {
	                    Class<?> clazz = fileSystem.loadFromJar(jarFile, ze.getName());
	                    Dag dag = clazz.getAnnotation(Dag.class);
	                    if (dag != null) {
	                        var map = new HashMap<String,String>();
	                        map.put("dagname", dag.name());
	                        map.put("groupname", dag.group());
	                        map.put("cronExpr", this.getRealCronExpr(dag.cronExpr()));
	                        map.put("onStart", dag.onStart());
	                        map.put("onEnd", dag.onEnd());
	                        map.put("owner", owner);
	                        String className = ze.getName().replace('/', '.');
	                        String finalname = className.substring(0, className.length() - CLASSEXT.length());
	                        if (finalname != null && !finalname.startsWith("bin")) {
	                            map.put(CLASSNAME, finalname);
	                        }
	                        classNames.add(map);
	                    }
	                }
	            }
	        }
	    } catch (Exception e) {
	    	log.error(e);       
	    	eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "analizeJar"));
	    }
	    return classNames;
	}


	private String getOwnerFromZip(ZipFile zfile) throws IOException {
		ZipEntry entry = zfile.getEntry(".owner");
		InputStream zipTmp = zfile.getInputStream(entry);
		BufferedReader reader1 = new BufferedReader(new InputStreamReader(zipTmp, StandardCharsets.UTF_8));
		String owner = reader1.readLine();
		reader1.close();
		zipTmp.close();
		return owner;
	}
	private String getRealCronExpr(String cronExpr) {
	    if (cronExpr.startsWith("${") && cronExpr.endsWith("}")) {
	        String key = cronExpr.substring(2, cronExpr.length() - 1); 
	        Environment env = ApplicationContextUtils.getApplicationContext().getBean(Environment.class);
	        return env.getProperty(key, cronExpr); 
	    }
	    return cronExpr;
	}
	@Override
	public Map<String,List<Map<String,String>>> getOperators(){
		return classMap;
	}
	@Override
	public void scheduler(String dagname,String jarname) throws DomainException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		Path jarfileO = this.findJarFile(jarname);
		Properties prop = this.getProperties(jarfileO);
		if(jarfileO!= null) {
			try {
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);
						Class<?> clazz = fileSystem.loadFromJar(jarfileO, classname);
						Dag toschedule = clazz.getAnnotation(Dag.class);
						if(toschedule.name().equals(dagname)) {
							quartz.propertiesToRepo(prop);
							DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
							if(toschedule.cronExpr().isEmpty()) {
								quartz.configureListener(toschedule,dag);	
							} else {
								quartz.activateJob( dag, toschedule.group());	
							}
						}
					
				}	
			} catch (Exception e) {
				throw new DomainException(ExceptionUtils.getRootCause(e)); 
			}	
		}
	}	
	private Path findJarFile(String jarFilename) {
	    Path jar = null;
	    for (Path path : jars) { 
	        if (path.getFileName().toString().equals(jarFilename)) {
	            jar = path;
	            break;
	        }
	    }
	    return jar;
	}
	@Override
	public void unschedule(String dagname, String jarname) throws DomainException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		Path jarfileO = this.findJarFile(jarname);
		if(jarfileO!=null) {
			try {		
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);
					Class<?> clazz = fileSystem.loadFromJar(jarfileO, classname);
					activateDeactivate(dagname, clazz);
				}		
			} catch (Exception e) {
				throw new DomainException(e);
			}	
		}
	}	
	private void activateDeactivate(String dagname,Class<?> clazz) {
		try {
			Dag toschedule = clazz.getAnnotation(Dag.class);
			if(toschedule.name().equals(dagname)) {
				DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
				if(toschedule.cronExpr().isEmpty()) {
					quartz.removeListener(toschedule);
				} else {
					quartz.deactivateJob(dag);	
				}
			}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "activateDeactivate"));
		}
	}
	public List<DagDTO> getDagDetail(String jarname) throws DomainException {
		if(jarname.equalsIgnoreCase("system")) {
			return this.getDefaultsSYSTEMS();
		} else {
			return this.getDagDetailJAR(jarname);
		}
	}
	private List<DagDTO> getDefaultsSYSTEMS() {
		List<DagDTO> defs = new ArrayList<>();
		List<String> ops = Arrays.asList("internal",LogsRollupOperator.class.getCanonicalName());
		List<String> register = Arrays.asList("register",RegisterSchedulerOperator.class.getCanonicalName());
		DagDTO item = new DagDTO();
		item.setDagname("background_system_dag");
		item.setCronExpr("0 0/10 * * * ?");
		item.setGroup("system_dags");
		DagDTO item2 = new DagDTO();
		item2.setDagname("event_system_dag");
		item2.setOnEnd("background_system_dag");
		item2.setGroup("system_dags");
		List<List<String>> list = new ArrayList<>();
		list.add(ops);
		list.add(register);
		item.setOps(list);
		
		defs.add(item);
		List<List<String>> liste = new ArrayList<>();
		List<String> evts = Arrays.asList("dummy",DummyOperator.class.getCanonicalName());
		liste.add(evts);
		item2.setOps(liste);
		defs.add(item2);
		
		return defs;
	}
	private List<DagDTO> getDagDetailJAR(String jarname) throws DomainException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		var result = new ArrayList<DagDTO>();
		Path jarfileO = this.findJarFile(jarname);
		if(jarfileO != null) {
			try {
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);	
					Class<?> clazz = fileSystem.loadFromJar(jarfileO, classname);
					Dag scheduled = clazz.getAnnotation(Dag.class);
					DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();	
					DagDTO dto = new DagDTO();
					dto.setDagname(scheduled.name());
					dto.setCronExpr(this.getRealCronExpr(scheduled.cronExpr()));
					dto.setGroup(scheduled.group());
					dto.setOnEnd(scheduled.onEnd());
					dto.setOnStart(scheduled.onStart());
					dto.setOps(dag.getDagGraph());
					result.add(dto);
				}	
			} catch (Exception e) {
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "getDagDetailJAR"));
			}	
		}
		return result;
	}
	
	public CompletableFuture<Map<String, DataFrame>> execute(String jarname, String dagname, String type, String data) throws DomainException {
	    try {
	        Path jarfileO = this.findJarFile(jarname);
	        if (jarfileO == null) {
	            return CompletableFuture.failedFuture(new DomainException(new Exception("Jarfile not found")));
	        }

	        Class<?> dagClass = loadDagClass(jarname, jarfileO, dagname);
	        DagExecutable dag = initializeDag(dagname,dagClass, type, data);

	        // Ejecutar el DAG
	        return quartz.executeInmediate(dag);
	    } catch (Exception e) {
	        return CompletableFuture.failedFuture(new DomainException(e));
	    }
	}

	private Class<?> loadDagClass(String jarname, Path jarfileO, String dagname) throws DomainException {
	    List<Map<String, String>> classNames = classMap.get(jarname);
	    for (Map<String, String> classMap1 : classNames) {
	        String classname1 = classMap1.get(CLASSNAME);
	        Class<?> clazz = fileSystem.loadFromJar(jarfileO, classname1);
	        Dag toschedule = clazz.getAnnotation(Dag.class);
	        if (toschedule.name().equals(dagname)) {
	            return clazz;
	        }
	    }
	    throw new DomainException(new Exception("dagname not found"));
	}

	private DagExecutable initializeDag(String dagname,Class<?> dagClass, String type, String data) throws DomainException {
	    try {
	    	DagExecutable dag = (DagExecutable) dagClass.getDeclaredConstructor().newInstance();
		    dag.setChannelData(data);
		    dag.setExecutionSource(type);
		    dag.setDagname(dagname);
		    return dag;	
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "initializeDag"));
			throw new DomainException(e);
		}
		
	}

	


	public List<Map<String,Object>> listScheduled() throws DomainException {
		try {
			return quartz.listScheduled();	
		} catch (Exception e) {
			throw new DomainException(e);
		} 
		
	}
	public String getIcons(String type) throws DomainException {
		try {
			OperatorStage instance = (OperatorStage) Class.forName(type).getDeclaredConstructor().newInstance();
			return instance.getIconImage();	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	public void deleteXCOM(Date time) throws DomainException {
		storage.deleteXCOM(time);
	}


	@Override
	public boolean isEnabled(String propKey) throws DomainException {
		var list = channels.availableChannels();
		for (Iterator<Class<? extends InputChannel>> iterator = list.iterator(); iterator.hasNext();) {
			Class<? extends InputChannel> class1 = iterator.next();
			if(	propKey.equals("ACTIVEMQ_PROPS") && class1.getCanonicalName().equals("main.cl.dagserver.infra.adapters.channels.ActiveMQChannel")) {
				return true;
			}
			if(propKey.equals("KAFKA_CONSUMER") && class1.getCanonicalName().equals("main.cl.dagserver.infra.adapters.channels.KafkaChannel")) {
				return true;
			}
			if(propKey.equals("REDIS_PROPS") && class1.getCanonicalName().equals("main.cl.dagserver.infra.adapters.channels.RedisChannel")) {
				return true;
			}
			if(propKey.equals("RABBIT_PROPS") && class1.getCanonicalName().equals("main.cl.dagserver.infra.adapters.channels.RabbitChannel")) {
				return true;
			}
		}
		return false;
	}


}
