package main.cl.dagserver.infra.adapters.output.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.domain.annotations.Dag;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.infra.adapters.confs.DagPathClassLoadHelper;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;
import main.cl.dagserver.infra.adapters.operators.DummyOperator;
import main.cl.dagserver.infra.adapters.operators.LogsRollupOperator;
import main.cl.dagserver.infra.adapters.operators.RegisterSchedulerOperator;
import main.cl.dagserver.infra.adapters.output.repositories.InternalStorage;

@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
public class JarSchedulerAdapter implements JarSchedulerOutputPort {
	@Autowired
	private InternalStorage storage;
	@Value("${param.folderpath}")
	private String pathfolder;
	@Autowired
    private ApplicationEventPublisher eventPublisher;
	@Autowired
	private QuartzConfig quartz;
	private static final String CLASSNAME = "classname";
	private static final String CLASSEXT = ".class";
	private DagPathClassLoadHelper helper = new DagPathClassLoadHelper();
	private List<File> jars = new ArrayList<>();
	private Map<String,List<Map<String,String>>> classMap = new HashMap<>();
	
	
	
	
	
	public JarSchedulerAdapter init () throws DomainException {
		this.classMap = new HashMap<>();
		File folder = new File(pathfolder);
		File[] listOfFiles = new File[0];
		try {
			listOfFiles = folder.listFiles();	
		} catch (Exception e) {
			log.error(e);
		}
			
		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].getName().endsWith(".jar")) {
				jars.add(listOfFiles[i]);
				classMap.put(listOfFiles[i].getName(), this.analizeJar(listOfFiles[i]));
				quartz.validate(listOfFiles[i].getName().replace(".jar", ""), this.analizeJarProperties(listOfFiles[i]));
			}
		}
		
		return this;
	}	
	
	private Map<String,Properties> analizeJarProperties(File jarFile){
		Map<String,Properties> props = new HashMap<>();
		try(
				ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
				ZipFile zipFile = new ZipFile(jarFile);) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();
				while(entries.hasMoreElements()) {
					 ZipEntry ze = entries.nextElement();
					 DagPathClassLoadHelper.verificationZipFile(ze, zipFile);
					 if (!ze.isDirectory() && ze.getName().endsWith(".properties")) {
					    	var prop = new Properties();
					    	prop.load(helper.loadResourceFromJar(jarFile, ze.getName()));
					    	String[] name = ze.getName().replace(".properties", "").split("/");
					    	props.put(name[name.length-1], prop);
					  }	 
				}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "analizeJarProperties"));
		}
		return props;
	}
	
	public Properties getProperties(File jarFile) {
		Properties prop = new Properties();
		try(
				ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
				ZipFile zipFile = new ZipFile(jarFile);
				) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				 ZipEntry ze = entries.nextElement();
				 DagPathClassLoadHelper.verificationZipFile(ze, zipFile);
				 if (!ze.isDirectory() && ze.getName().endsWith("properties")) {
					 InputStream inputStream = zipFile.getInputStream(ze);
		             prop.load(inputStream);
		             inputStream.close();
				 }
			}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "getProperties"));
		}
		return prop;
	}
	private List<Map<String,String>> analizeJar(File jarFile) {
		List<Map<String,String>> classNames = new ArrayList<>();
		try(
				ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
				ZipFile zipFile = new ZipFile(jarFile);
				) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
				 ZipEntry ze = entries.nextElement();
				 DagPathClassLoadHelper.verificationZipFile(ze, zipFile);
				 if (!ze.isDirectory() && ze.getName().endsWith(CLASSEXT)) {
					 	Class<?> clazz = helper.loadFromJar(jarFile, ze.getName()); 
				    	Dag dag = clazz.getAnnotation(Dag.class);
				        if(dag!=null) {
				        	var map = new HashMap<String,String>();
					        map.put("dagname", dag.name());
					        map.put("groupname", dag.group());
					        map.put("cronExpr", dag.cronExpr());
					        map.put("onStart", dag.onStart());
					        map.put("onEnd", dag.onEnd());
					        String className = ze.getName().replace('/', '.'); 
					        String finalname = className.substring(0, className.length() - CLASSEXT.length());
					        if(finalname != null && !finalname.startsWith("bin")) {
					        	map.put(CLASSNAME, finalname);	
					        }
					        classNames.add(map);	
				        }
				    }
			}
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "analizeJar"));
		}
		return classNames;
	}
	@Override
	public Map<String,List<Map<String,String>>> getOperators(){
		return classMap;
	}
	@Override
	public void scheduler(String dagname,String jarname) throws DomainException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		File jarfileO = this.findJarFile(jarname);
		Properties prop = this.getProperties(jarfileO);
		if(jarfileO!= null) {
			try {
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);
						Class<?> clazz = helper.loadFromJar(jarfileO, classname);
						Dag toschedule = clazz.getAnnotation(Dag.class);
						if(toschedule.name().equals(dagname)) {
							quartz.propertiesToRepo(prop);
							DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
							if(toschedule.cronExpr().equals("")) {
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
	private File findJarFile(String jarFilename) {
		File jar = null;
		for (Iterator<File> iterator = jars.iterator(); iterator.hasNext();) {
			File file = iterator.next();
			if(file.getName().equals(jarFilename)) {
				jar = file;
				break;
			}
		}
		return jar;
	}
	@Override
	public void unschedule(String dagname, String jarname) throws DomainException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		File jarfileO = this.findJarFile(jarname);
		if(jarfileO!=null) {
			try {		
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);
					Class<?> clazz = helper.loadFromJar(jarfileO, classname);
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
				if(toschedule.cronExpr().equals("")) {
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
		File jarfileO = this.findJarFile(jarname);
		if(jarfileO != null) {
			try {
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);	
					Class<?> clazz = helper.loadFromJar(jarfileO, classname);
					Dag scheduled = clazz.getAnnotation(Dag.class);
					DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();	
					DagDTO dto = new DagDTO();
					dto.setDagname(scheduled.name());
					dto.setCronExpr(scheduled.cronExpr());
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
	
	@SuppressWarnings("resource")
	public void execute(String jarname, String dagname, String type, String data) throws DomainException {
		try {
			List<Map<String,String>> classNames = classMap.get(jarname);
			File jarfileO = this.findJarFile(jarname);
			if(jarfileO!= null) {
				Boolean founded = false;
				for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
					String classname = iterator.next().get(CLASSNAME);
					Class<?> clazz = this.helper.loadFromJar(jarfileO, classname);
					Dag toschedule = clazz.getAnnotation(Dag.class);
					if(toschedule.name().equals(dagname)) {
						founded = true;
						DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
						dag.setChannelData(data);
						dag.setExecutionSource(type);
						quartz.executeInmediate(dag);
						break;
					}
				}
				if(Boolean.FALSE.equals(founded)) {
					throw new DomainException(new Exception("dagname not found"));
				}	
			}	
		} catch (Exception e) {
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
