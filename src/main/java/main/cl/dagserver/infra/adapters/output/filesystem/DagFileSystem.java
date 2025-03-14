package main.cl.dagserver.infra.adapters.output.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.linkedin.cytodynamics.nucleus.DelegateRelationshipBuilder;
import com.linkedin.cytodynamics.nucleus.IsolationLevel;
import com.linkedin.cytodynamics.nucleus.LoaderBuilder;
import com.linkedin.cytodynamics.nucleus.OriginRestriction;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.FileEntryDTO;

@Log4j2
public abstract class DagFileSystem implements FileSystemOutputPort {
	
	
	private static final String CLASSEXT = ".class";
	private static final String SEP = "/";
	
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
	public Class<?> loadFromJar(Path jarFile,String name) throws DomainException{
			URI uri = jarFile.toUri();
			ClassLoader loader = LoaderBuilder
				    .anIsolatingLoader()
				    .withOriginRestriction(OriginRestriction.allowByDefault())
				    .withClasspath(Arrays.asList( uri ))
				    .withParentRelationship(DelegateRelationshipBuilder.builder()
				        .withIsolationLevel(IsolationLevel.NONE)
				        .build())
				    .build();
			try {
				return loader.loadClass(name.replace(SEP , ".").replace(CLASSEXT, ""));
			} catch (ClassNotFoundException e) {
				throw new DomainException(e);
			}
			
		}
	public InputStream loadResourceFromJar(Path jarFile, String resource) {
			URI uri = jarFile.toUri();
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
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException {
		
		ClassLoader loader = LoaderBuilder
				    .anIsolatingLoader()
				    .withOriginRestriction(OriginRestriction.allowByDefault())
				    .withClasspath(list)
				    .withParentRelationship(DelegateRelationshipBuilder.builder()
				        .withIsolationLevel(IsolationLevel.NONE)
				        .build())
				    .build();
			try {
				URL[] urls = ((URLClassLoader) loader).getURLs();
				for (URL url : urls) {
				    log.info("Classpath: " + url);
				}
				return loader.loadClass(name.replace(File.separator , ".").replace(CLASSEXT, ""));
			} catch (ClassNotFoundException e) {
				throw new DomainException(e);
			}
		
			
			
			
		}

	protected List<FileEntryDTO> getFileEntries(Path directory) throws DomainException {
	    List<FileEntryDTO> fileEntries = new ArrayList<>();
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
	        for (Path entry : stream) {
	        	FileEntryDTO fileEntry = new FileEntryDTO();
	            fileEntry.setFilename(entry.getFileName().toString());       
	            if (Files.isDirectory(entry)) {
	                fileEntry.setType("folder");
	                fileEntry.setContent(getFileEntries(entry));
	            } else {
	                fileEntry.setType("file");
	                fileEntry.setContent(null);
	            }
	            
	            fileEntries.add(fileEntry);
	        }
	    } catch (IOException e) {
	       throw new DomainException(e);
	    }
	    return fileEntries;
	}
	
	public void createFolder(String foldername) throws DomainException {
	    try {
	        Path folderPath = this.getFolderPath(foldername);
	        Files.createDirectories(folderPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}

	public void delete(String folder, String file) throws DomainException {
	    try {
	        Path targetPath;
	        if (file == null || file.isEmpty()) {
	            targetPath = this.getFolderPath(folder);
	        } else {
	            String realpath = (folder + SEP + file).replace("//", SEP ).replace("/\\", SEP);            
	            targetPath = this.getFolderPath(realpath);
	        }
	        Files.delete(targetPath);
	    } catch (IOException e) {
	        throw new DomainException(e);
	    }
	}
}
