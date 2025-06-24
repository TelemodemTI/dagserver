package main.cl.dagserver.infra.adapters.output.scheduler;

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
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;
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
	
	
	private static final String CLASSNAME = "classname";
	private static final String CLASSEXT = ".class";
	
	private List<Path> jars = new ArrayList<>();
	private Map<String,List<Map<String,String>>> classMap = new HashMap<>();
	
	public JarSchedulerAdapter init() throws DomainException {
	    this.classMap = new HashMap<>();
	    this.jars = new ArrayList<>();
	    Path folderPath = fileSystem.getFolderPath();
	    List<Path> jarFiles = new ArrayList<>();
	    try (Stream<Path> paths = Files.walk(folderPath)) {
	        jarFiles = paths
	        .filter(path -> path.toString().endsWith(".jar"))
            .filter(path -> {
                return filtrarEsDag(path);
            })
	        .collect(Collectors.toList());
	        for (Path jarFile : jarFiles) {
		        
		    	jars.add(jarFile);
		        classMap.put(jarFile.getFileName().toString(), this.analizeJar(jarFile));
		        quartz.validate(jarFile.getFileName().toString().replace(".jar", ""), this.analizeJarProperties(jarFile));
		    }

		    return this;
	    } catch (IOException e) {
	        log.error("JarSchedulerAdapter init:", e);
	        return this;
	    }
	}


	private boolean filtrarEsDag(Path jarFile) {
		try (
			InputStream inputStream = Files.newInputStream(jarFile);
			ZipInputStream zip = new ZipInputStream(inputStream);) {
			ZipEntry ze;
			while ((ze = zip.getNextEntry()) != null) {
				 if (!ze.isDirectory() && ze.getName().endsWith(".source")) {
					InputStream zip1 = fileSystem.loadResourceFromJar(jarFile, ze.getName());
					BufferedReader reader = new BufferedReader(new InputStreamReader(zip1, StandardCharsets.UTF_8));
					String content = reader.readLine();
					reader.close();
					return "dagserver-generated-jar".equals(content);	
				}
			}
			return false;
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
	private List<Map<String,String>> analizeJar(Path jarFile) throws IOException, DomainException {
		List<Map<String,String>> classNames = new ArrayList<>();
	    InputStream inputStream = Files.newInputStream(jarFile);
		ZipInputStream zip = new ZipInputStream(inputStream); 
		ZipEntry ze;
	    	String owner = getOwnerFromZip(jarFile);
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
	        
	    zip.close();
	    inputStream.close();
	    return classNames;
	}


	private String getOwnerFromZip(Path jarFile) throws IOException{
		
		InputStream inputStream = Files.newInputStream(jarFile);
		ZipInputStream zip = new ZipInputStream(inputStream);
		ZipEntry ze;
		while ((ze = zip.getNextEntry()) != null) {
			if (!ze.isDirectory() && ze.getName().endsWith(".owner")) {
					InputStream zip1 = fileSystem.loadResourceFromJar(jarFile, ze.getName());
					BufferedReader reader = new BufferedReader(new InputStreamReader(zip1, StandardCharsets.UTF_8));
					String owner = reader.readLine(); 
					zip.close();
					reader.close();
					inputStream.close();
					return owner;
			}
		}
		zip.close();
		inputStream.close();
		return "";
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
		Path jarfileO = this.findJarFileFecha(jarname);
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
								quartz.configureListener(toschedule,dag,jarname);	
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
	private Path findJarFileFecha(String jarFilename) {
		Path jar = null;
	    for (Path path : jars) { 
	        if (path.getFileName().toString().trim().equals(jarFilename.trim())) {
	            jar = path;
	            break;
	        }
	    }
	    return jar;
	}
	
	private Path findJarFile(String jarFilename) {
		Path jar = null;
	    for (Path path : jars) { 
	    	
	    	String valorFD = path.getFileName().toString().trim();
	    	Integer posicion = valorFD.indexOf(".");
			String nombreJar = valorFD.substring(posicion +1);
	        if (nombreJar.equals(jarFilename.trim())) {
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
		Path jarfileO = this.findJarFileFecha(jarname);
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
	        Path jarfileO = this.findJarFileFecha(jarname);
	        if (jarfileO == null) {
	            return CompletableFuture.failedFuture(new DomainException(new Exception("Jarfile not found")));
	        }

	        Class<?> dagClass = loadDagClass(jarfileO.getFileName().toString(), jarfileO, dagname);
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

}
