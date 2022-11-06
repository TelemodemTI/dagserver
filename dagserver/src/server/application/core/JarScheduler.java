package server.application.core;

import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import org.quartz.Job;

import server.infra.annotations.Dag;
import server.infra.quartz.QuartzConfig;

public class JarScheduler {
	private static Logger log = Logger.getLogger(JarScheduler.class);
	
	private List<File> jars = new ArrayList<File>();
	private Map<String,List<Map<String,String>>> classMap = new HashMap<String,List<Map<String,String>>>();
	private QuartzConfig quartz;
	public JarScheduler(String pathfolder,QuartzConfig quartz) throws Exception {
		this.quartz = quartz;
		File folder = new File(pathfolder);
		File[] listOfFiles = folder.listFiles();	
		for (int i = 0; i < listOfFiles.length; i++) {
			if(listOfFiles[i].getName().endsWith(".jar")) {
				jars.add(listOfFiles[i]);
				classMap.put(listOfFiles[i].getName(), this.analizeJar(listOfFiles[i]));
			}
		}
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
		return classNames;
	}
	public Map<String,List<Map<String,String>>> getOperators(){
		return classMap;
	}
	public void scheduler(String dagname,String jarname) throws MalformedURLException {
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
						quartz.configureListener(toschedule,dag);	
					} else {
						quartz.activateJob((Job) dag, toschedule.group());	
					}
					log.debug("job scheduled!::");
				}
			} catch (Exception e) {
				e.printStackTrace();
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
	public void unschedule(String dagname, String jarname) throws MalformedURLException {
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
	}	
}
