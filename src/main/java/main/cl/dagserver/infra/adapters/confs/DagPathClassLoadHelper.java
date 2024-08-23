package main.cl.dagserver.infra.adapters.confs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.quartz.simpl.CascadingClassLoadHelper;
import org.springframework.context.ApplicationContext;

import com.linkedin.cytodynamics.nucleus.DelegateRelationshipBuilder;
import com.linkedin.cytodynamics.nucleus.IsolationLevel;
import com.linkedin.cytodynamics.nucleus.LoaderBuilder;
import com.linkedin.cytodynamics.nucleus.OriginRestriction;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;


public class DagPathClassLoadHelper extends CascadingClassLoadHelper {

	
	private static final String CLASSEXT = ".class";
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		var prop = new Properties();
		try {
			return super.loadClass(name);	
		} catch (Exception e) {
			return this.loadClassProps(prop,name);
		}
	}
	
	private Class<?> loadClassProps(Properties prop,String name) {
			try {
				var ctx = this.getClass().getClassLoader();
				return this.loadClassWithCtx(ctx,prop,name);
			} catch (Exception e) {
				return null;
			}	
	}
	@SuppressWarnings("static-access")
	private Class<?> loadClassWithCtx(ClassLoader ctx,Properties prop,String name) throws IOException, ClassNotFoundException, DomainException {
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		if(ctx !=null && appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			Path folderPath = handler.getFolderPath();
			try {
				return this.getClassForLoad(folderPath, name);	
			} catch (Exception e) {
				List<String> archivosJar = new ArrayList<>();
				this.searchJarFiles(folderPath.toFile(),archivosJar);
				List<URI> list = new ArrayList<>();
				for (Iterator<String> iterator = archivosJar.iterator(); iterator.hasNext();) {
					String jarpath = iterator.next();
					list.add(new File(jarpath).toURI());
				}
				
				ClassLoader cls = handler.getClassLoader(list);
				return cls.loadClass(name);
				
			}
		} else {
			throw new DomainException(new Exception("no context?"));
		}
	}
	private void searchJarFiles(File directorio, List<String> archivosJar) {
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().endsWith(".jar")) {
                    archivosJar.add(archivo.getAbsolutePath());
                } else if (archivo.isDirectory()) {
                	searchJarFiles(archivo, archivosJar);
                }
            }
        }
    }

	private Class<?> getClassForLoad(Path folderPath, String name) throws DomainException {
	    try {
	        File folder = folderPath.toFile();
	        File[] listOfFiles = folder.listFiles();	
	        for (int i = 0; i < listOfFiles.length; i++) {
	            if (listOfFiles[i].getName().endsWith(".jar")) {
	                Class<?> rv = this.search(listOfFiles[i], name);
	                if (rv != null) {
	                    return rv;	
	                } 
	            }
	        }
	        return super.loadClass(name);	
	    } catch (Exception e) {
	        throw new DomainException(e);
	    }
	}
	private Class<?> search(File jarFile,String searched) throws DomainException {
		Class<?> rvclazz = null;
		try(
			ZipInputStream zip = new ZipInputStream(new FileInputStream(jarFile.getAbsoluteFile()));
		) {
			if(this.isGeneratedByDagserver(jarFile)) {
				rvclazz = this.validate(jarFile,searched);	
			}
			return rvclazz;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}	
	private boolean isGeneratedByDagserver(File jarFile) throws DomainException {
		Boolean returnf = false;
		try(ZipFile zipFile = new ZipFile(jarFile);) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
			  ZipEntry ze = entries.nextElement();
			  if(ze.getName().equals(".source")) {
				  InputStream inputStream = zipFile.getInputStream(ze);
	                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

	                StringBuilder stringBuilder = new StringBuilder();
	                String linea;
	                while ((linea = bufferedReader.readLine()) != null) {
	                    stringBuilder.append(linea);
	                    stringBuilder.append("\n");
	                }
	                String contenido = stringBuilder.toString();
	                if(contenido.equals("dagserver-generated-jar")) {
	                	returnf = true;
	                } else {
	                	returnf = false;
	                }
			  } else {
				  returnf = false;
			  }
			}
		} catch (Exception e) {
			throw new DomainException(e);
		}
		return returnf;
	}
	private Class<?> validate(File f,String searched) throws IOException, DomainException {
		Class<?> rvclazz = null;
		
		URI uri = f.toURI();
		ClassLoader loader = LoaderBuilder
			    .anIsolatingLoader()
			    .withOriginRestriction(OriginRestriction.allowByDefault())
			    .withClasspath(Arrays.asList( uri ))
			    .withParentRelationship(DelegateRelationshipBuilder.builder()
			        .withIsolationLevel(IsolationLevel.FULL)
			        .build())
			    .build();
		
		try(ZipFile zipFile = new ZipFile(f);) {
			Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while(entries.hasMoreElements()) {
			  ZipEntry ze = entries.nextElement();
			  ZipFileVerificator.verificationZipFile(ze, zipFile);
			  if (!ze.isDirectory() && ze.getName().endsWith(CLASSEXT)) {
				  rvclazz = this.loadClassTry(ze,loader,searched);	
			  }
			}
			return rvclazz;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	private Class<?> loadClassTry(ZipEntry ze,ClassLoader loader,String searched) throws DomainException {
		Class<?> rvclazz = null;
		try {
			  Class<?> clazz = loader.loadClass(ze.getName().replace("/", ".").replace(CLASSEXT, ""));
		      if(clazz.getName().equals(searched)) {
		    	rvclazz = clazz;
		      }
		      return rvclazz;
			} catch (NoClassDefFoundError | ClassNotFoundException e) {
				throw new DomainException(e);
			}
	}
	
	
	
	
	
	
	
}
