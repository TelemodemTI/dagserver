package main.infra.adapters.confs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.apache.log4j.Logger;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.quartz.spi.ClassLoadHelper;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import main.domain.exceptions.DomainException;

public class DagPathClassLoadHelper extends CascadingClassLoadHelper implements ClassLoadHelper {

	private static Logger log = Logger.getLogger(DagPathClassLoadHelper.class);
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		var prop = new Properties();
		var context = ContextLoader.getCurrentWebApplicationContext();
		if(context!=null){
			return this.loadClassProps(prop,context,name);
		}
		return super.loadClass(name);
	}
	private Class<?> loadClassProps(Properties prop,WebApplicationContext context,String name) {
		var srv = context.getServletContext();
		if(srv != null) {
			try {
				ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(srv);
				var ctx = (springContext != null)? springContext.getClassLoader():null;
				if(ctx !=null) {
					return this.getClassForLoad(prop, getClassLoader(), name);	
				} else {
					log.error("no existe contexto??");
					return null;
				}
			} catch (Exception e) {
				log.error(e);
				return null;
			}	
		} else return null;
	}
	private Class<?> getClassForLoad(Properties prop,ClassLoader ctx, String name) throws Exception {
		prop.load(ctx.getResourceAsStream("application.properties"));	
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
	}
	private Class<?> search(File jarFile,String searched) throws DomainException {
		Class<?> rvclazz = null;
		try(
				URLClassLoader cl = new URLClassLoader(new URL[]{jarFile.toURI().toURL()},this.getClass().getClassLoader());
				ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
		) {
			rvclazz = this.validate(jarFile,searched,cl);
		} catch (Exception e) {
			log.error(e);
		}
		return rvclazz;
	}	
	private Class<?> validate(File f,String searched,URLClassLoader cl) throws IOException, DomainException {
		Class<?> rvclazz = null;
		try(ZipFile zipFile = new ZipFile(f);) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
			  ZipEntry ze = entries.nextElement();
			  DagPathClassLoadHelper.verificationZipFile(ze, zipFile);
			  if (!ze.isDirectory() && ze.getName().endsWith(".class")) {
			    	Class<?> clazz = cl.loadClass(ze.getName().replace("/", ".").replace(".class", ""));
			    	if(clazz.getName().equals(searched)) {
			    		rvclazz = clazz;
			    		break;
			    	}
			   }
			}
			return rvclazz;
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	public static void verificationZipFile(ZipEntry ze,ZipFile zipFile) throws DomainException {
		int thresholdEntries = 10000;
		int thresholdSize = 1000000000; // 1 GB
		double thresholdRatio = 10;
		
		
		
		
		
		
		int totalSizeArchive = 0;
		int totalEntryArchive = 0;
		try(
				InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));
				OutputStream out = new BufferedOutputStream(new FileOutputStream("./output_onlyfortesting.txt"));) {
				  totalEntryArchive ++;

				  int nBytes = -1;
				  byte[] buffer = new byte[2048];
				  double totalSizeEntry = 0;

				  while((nBytes = in.read(buffer)) > 0) { // Compliant
				      out.write(buffer, 0, nBytes);
				      totalSizeEntry += nBytes;
				      totalSizeArchive += nBytes;
				      Long tmpv = ze.getCompressedSize();
				      double compressionRatio = totalSizeEntry / tmpv.doubleValue();
				      if(compressionRatio > thresholdRatio) {
				        // ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
				    	throw new DomainException("invalid zip file");
				      }
				  }

				  if(totalSizeArchive > thresholdSize) {
				      // the uncompressed data size is too much for the application resource capacity
					  throw new DomainException("zip file invalid size");
				  }

				  if(totalEntryArchive > thresholdEntries) {
				      // too much entries in this archive, can lead to inodes exhaustion of the system
					  throw new DomainException("zip file invalid entries");
				  }
			  } catch (Exception e) {
				  throw new DomainException(e.getMessage());
			  }
	}
}
