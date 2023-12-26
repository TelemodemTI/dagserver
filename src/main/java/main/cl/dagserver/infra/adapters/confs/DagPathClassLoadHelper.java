package main.cl.dagserver.infra.adapters.confs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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
import org.quartz.spi.ClassLoadHelper;
import com.linkedin.cytodynamics.nucleus.DelegateRelationshipBuilder;
import com.linkedin.cytodynamics.nucleus.IsolationLevel;
import com.linkedin.cytodynamics.nucleus.LoaderBuilder;
import com.linkedin.cytodynamics.nucleus.OriginRestriction;
import main.cl.dagserver.domain.exceptions.DomainException;


public class DagPathClassLoadHelper extends CascadingClassLoadHelper implements ClassLoadHelper {

	
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
	private Class<?> loadClassWithCtx(ClassLoader ctx,Properties prop,String name) throws IOException, ClassNotFoundException, DomainException {
		if(ctx !=null) {
			prop.load(ctx.getResourceAsStream("application.properties"));	
			String pathfolder = prop.getProperty("param.folderpath");
			try {
				return this.getClassForLoad(pathfolder, name);	
			} catch (Exception e) {
				List<String> archivosJar = new ArrayList<>();
				this.searchJarFiles(new File(pathfolder),archivosJar);
				List<URI> list = new ArrayList<>();
				for (Iterator<String> iterator = archivosJar.iterator(); iterator.hasNext();) {
					String jarpath = iterator.next();
					list.add(new File(jarpath).toURI());
				}
				ClassLoader cls = this.getClassLoader(list);
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

	private Class<?> getClassForLoad(String pathfolder, String name) throws DomainException {
		try {
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
			  DagPathClassLoadHelper.verificationZipFile(ze, zipFile);
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
	public static void verificationZipFile(ZipEntry ze,ZipFile zipFile) throws DomainException {
		int thresholdEntries = 10000;
		int thresholdSize = 1000000000; // 1 GB
		double thresholdRatio = 10;
		
		
		
		
		
		
		int totalSizeArchive = 0;
		int totalEntryArchive = 0;
		try(
				InputStream in = new BufferedInputStream(zipFile.getInputStream(ze));
				) {
				  totalEntryArchive ++;

				  int nBytes = -1;
				  byte[] buffer = new byte[2048];
				  double totalSizeEntry = 0;

				  while((nBytes = in.read(buffer)) > 0) { 
				      totalSizeEntry += nBytes;
				      totalSizeArchive += nBytes;
				      Long tmpv = ze.getCompressedSize();
				      double compressionRatio = totalSizeEntry / tmpv.doubleValue();
				      if(compressionRatio > thresholdRatio) {
				        // ratio between compressed and uncompressed data is highly suspicious, looks like a Zip Bomb Attack
				    	throw new DomainException(new Exception("invalid zip file"));
				      }
				  }

				  if(totalSizeArchive > thresholdSize) {
				      // the uncompressed data size is too much for the application resource capacity
					  throw new DomainException(new Exception("zip file invalid size"));
				  }

				  if(totalEntryArchive > thresholdEntries) {
				      // too much entries in this archive, can lead to inodes exhaustion of the system
					  throw new DomainException(new Exception("zip file invalid entries"));
				  }
			  } catch (Exception e) {
				  throw new DomainException(e);
			  }
	}

	public ClassLoader getClassLoader(List<URI> list) {
		return LoaderBuilder
			    .anIsolatingLoader()
			    .withOriginRestriction(OriginRestriction.allowByDefault())
			    .withClasspath(list)
			    .withParentRelationship(DelegateRelationshipBuilder.builder()
			        .withIsolationLevel(IsolationLevel.NONE)
			        .build())
			    .build();
	}
	
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException{
		ClassLoader loader = LoaderBuilder
			    .anIsolatingLoader()
			    .withOriginRestriction(OriginRestriction.allowByDefault())
			    .withClasspath(list)
			    .withParentRelationship(DelegateRelationshipBuilder.builder()
			        .withIsolationLevel(IsolationLevel.NONE)
			        .build())
			    .build();
		try {
			return loader.loadClass(name.replace("/", ".").replace(CLASSEXT, ""));
		} catch (ClassNotFoundException e) {
			throw new DomainException(e);
		}
		
	}
	
	public Class<?> loadFromJar(File jarFile,String name) throws DomainException{
		URI uri = jarFile.toURI();
		ClassLoader loader = LoaderBuilder
			    .anIsolatingLoader()
			    .withOriginRestriction(OriginRestriction.allowByDefault())
			    .withClasspath(Arrays.asList( uri ))
			    .withParentRelationship(DelegateRelationshipBuilder.builder()
			        .withIsolationLevel(IsolationLevel.NONE)
			        .build())
			    .build();
		try {
			return loader.loadClass(name.replace("/", ".").replace(CLASSEXT, ""));
		} catch (ClassNotFoundException e) {
			throw new DomainException(e);
		}
		
	}
	public InputStream loadResourceFromJar(File jarFile, String resource) {
		URI uri = jarFile.toURI();
		ClassLoader loader = LoaderBuilder
			    .anIsolatingLoader()
			    .withOriginRestriction(OriginRestriction.allowByDefault())
			    .withClasspath(Arrays.asList(  uri ))
			    .withParentRelationship(DelegateRelationshipBuilder.builder()
			        .withIsolationLevel(IsolationLevel.FULL)
			        .build())
			    .build();
		return loader.getResourceAsStream(resource);
	}
}
