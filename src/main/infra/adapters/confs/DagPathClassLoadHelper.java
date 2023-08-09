package main.infra.adapters.confs;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DagPathClassLoadHelper extends CascadingClassLoadHelper implements ClassLoadHelper {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(DagPathClassLoadHelper.class);
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		
		ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext().getServletContext());
		
		var prop = new Properties();

		try {
			prop.load(springContext.getClassLoader().getResourceAsStream("application.properties"));	
			String pathfolder = prop.getProperty("param.folderpath");
			File folder = new File(pathfolder);
			File[] listOfFiles = folder.listFiles();	
			for (int i = 0; i < listOfFiles.length; i++) {
				if(listOfFiles[i].getName().endsWith(".jar")) {
					Class<?> rv = this.search(listOfFiles[i], name);
					if(rv != null) {
						return rv;	
					} 
				}
			}
			return super.loadClass(name);
		} catch (Exception e) {
			log.error(e);
		}
		return super.loadClass(name);
	}
	private Class<?> search(File jarFile,String searched) throws Exception {
		Class<?> rvclazz = null;
		try(URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},this.getClass().getClassLoader());) {
			ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
			for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
			    if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
			        // This ZipEntry represents a class. Now, what class does it represent?
			    	Class<?> clazz = cl.loadClass(entry.getName().replace("/", ".").replace(".class", ""));
			    	if(clazz.getName().equals(searched)) {
			    		rvclazz = clazz;
			    		break;
			    	}
			    }
			}	
		} catch (Exception e) {
			log.error(e);
		}
		return rvclazz;
	}
}
