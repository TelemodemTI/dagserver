package main.infra.adapters.output.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.output.JarSchedulerOutputPort;
import main.domain.annotations.Dag;
import main.domain.core.DagExecutable;
import main.domain.model.DagDTO;
import main.infra.adapters.confs.QuartzConfig;
import main.infra.adapters.operators.Junit5SuiteOperator;
import main.infra.adapters.operators.LogsRollupOperator;
import main.infra.adapters.operators.RegisterSchedulerOperator;

@Component
@ImportResource("classpath:properties-config.xml")
public class JarSchedulerAdapter implements JarSchedulerOutputPort {
	
	@Value("${param.folderpath}")
	private String pathfolder;
	
	@Autowired
	QuartzConfig quartz;
		
	private static Logger log = Logger.getLogger(JarSchedulerAdapter.class);
	
	private List<File> jars = new ArrayList<File>();
	private Map<String,List<Map<String,String>>> classMap = new HashMap<String,List<Map<String,String>>>();
	
	public JarSchedulerAdapter init () throws Exception {
		classMap =  new HashMap<String,List<Map<String,String>>>();
		File folder = new File(pathfolder);
		File[] listOfFiles = folder.listFiles();	
		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].getName().endsWith(".jar")) {
				jars.add(listOfFiles[i]);
				classMap.put(listOfFiles[i].getName(), this.analizeJar(listOfFiles[i]));
				quartz.validate(listOfFiles[i].getName().replace(".jar", ""), this.analizeJarProperties(listOfFiles[i]));
			}
		}
		
		return this;
	}	
	
	private Map<String,Properties> analizeJarProperties(File jarFile) throws Exception{
		URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},this.getClass().getClassLoader());
		ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
		Map<String,Properties> props = new HashMap<String,Properties>();
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			if (!entry.isDirectory() && entry.getName().endsWith(".properties")) {
		    	var prop = new Properties();
		    	prop.load(cl.getResourceAsStream(entry.getName()));
		    	String[] name = entry.getName().replace(".properties", "").split("/");
		    	props.put(name[name.length-1], prop);
		    }
		}
		cl.close();
		return props;
	}
	
	
	private List<Map<String,String>> analizeJar(File jarFile) throws Exception{
		URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},this.getClass().getClassLoader());
		List<Map<String,String>> classNames = new ArrayList<Map<String,String>>();
		ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
		for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
		    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
		        // This ZipEntry represents a class. Now, what class does it represent?
		    	Class<?> clazz = cl.loadClass(entry.getName().replace("/", ".").replace(".class", ""));
		        
		    	Dag dag = clazz.getAnnotation(Dag.class);
		        var map = new HashMap<String,String>();
		        map.put("dagname", dag.name());
		        map.put("groupname", dag.group());
		        map.put("cronExpr", dag.cronExpr());
		        map.put("onStart", dag.onStart());
		        map.put("onEnd", dag.onEnd());
		        String className = entry.getName().replace('/', '.'); // including ".class"
		        String finalname = className.substring(0, className.length() - ".class".length());
		        if(finalname != null && !finalname.startsWith("bin")) {
		        	map.put("classname", finalname);	
		        }
		        classNames.add(map);
		    }
		}
		cl.close();
		return classNames;
	}
	@Override
	public Map<String,List<Map<String,String>>> getOperators(){
		return classMap;
	}
	@Override
	public void scheduler(String dagname,String jarname) throws Exception {
		List<Map<String,String>> classNames = classMap.get(jarname);
		File jarfileO = this.findJarFile(jarname);
		URLClassLoader cl = new URLClassLoader(new URL[]{jarfileO.toURI().toURL()},this.getClass().getClassLoader());
		for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
			String classname = iterator.next().get("classname");
			
				Class<?> clazz = cl.loadClass(classname);
				Dag toschedule = clazz.getAnnotation(Dag.class);
				if(toschedule.name().equals(dagname)) {
					DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
					
					if(toschedule.cronExpr().equals("")) {
						quartz.configureListener(toschedule,dag);	
					} else {
						quartz.activateJob((Job) dag, toschedule.group());	
					}
					log.debug("job scheduled!::");
				}
			
		}
		cl.close();
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
	public void unschedule(String dagname, String jarname) throws IOException {
		List<Map<String,String>> classNames = classMap.get(jarname);
		File jarfileO = this.findJarFile(jarname);
		URLClassLoader cl = new URLClassLoader(new URL[]{jarfileO.toURI().toURL()},this.getClass().getClassLoader());
		for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
			String classname = iterator.next().get("classname");
			try {
				Class<?> clazz = cl.loadClass(classname);
				Dag toschedule = clazz.getAnnotation(Dag.class);
				if(toschedule.name().equals(dagname)) {
					DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
					if(toschedule.cronExpr().equals("")) {
						quartz.removeListener(toschedule, dag);
					} else {
						quartz.deactivateJob(dag);	
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		cl.close();
	}	
	
	public List<DagDTO> getDagDetail(String jarname) throws Exception {
		if(jarname.toLowerCase().equals("system")) {
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
		item.setOps(new ArrayList<List<String>>() {
			private static final long serialVersionUID = 1L;
		{
			add(ops);
			add(register);
		}});
		List<String> step1 = Arrays.asList("local_testing",Junit5SuiteOperator.class.getCanonicalName());
		DagDTO evt = new DagDTO();
		evt.setDagname("event_system_dag");
		evt.setGroup("system_dags");
		evt.setOnEnd("background_system_dag");
		evt.setOps(new ArrayList<List<String>>() {
			private static final long serialVersionUID = 1L;
		{
			add(step1);
		}});
		defs.add(item);
		defs.add(evt);
		return defs;
	}
	private List<DagDTO> getDagDetailJAR(String jarname) throws Exception {
		List<Map<String,String>> classNames = classMap.get(jarname);
		var result = new ArrayList<DagDTO>();
		File jarfileO = this.findJarFile(jarname);
		URLClassLoader cl = new URLClassLoader(new URL[]{jarfileO.toURI().toURL()},this.getClass().getClassLoader());
		for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
			String classname = iterator.next().get("classname");
			
			Class<?> clazz = cl.loadClass(classname);
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
		cl.close();
		return result;
	}
	
	@SuppressWarnings("resource")
	public void execute(String jarname, String dagname) throws Exception {
		List<Map<String,String>> classNames = classMap.get(jarname);
		log.debug(jarname);
		File jarfileO = this.findJarFile(jarname);
		URLClassLoader cl = new URLClassLoader(new URL[]{jarfileO.toURI().toURL()},this.getClass().getClassLoader());
		Boolean founded = false;
		
		for (Iterator<Map<String,String>> iterator = classNames.iterator(); iterator.hasNext();) {
			String classname = iterator.next().get("classname");
			Class<?> clazz = cl.loadClass(classname);
			Dag toschedule = clazz.getAnnotation(Dag.class);
			if(toschedule.name().equals(dagname)) {
				founded = true;
				DagExecutable dag = (DagExecutable) clazz.getDeclaredConstructor().newInstance();
				quartz.executeInmediate(dag);
				break;
			}
		}
		if(!founded) {
			throw new Exception("dagname not found");
		} 
	}
	
	public List<Map<String,Object>> listScheduled() throws Exception {
		 return quartz.listScheduled();
	}
}
