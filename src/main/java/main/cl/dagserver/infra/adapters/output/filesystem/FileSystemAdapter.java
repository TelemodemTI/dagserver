package main.cl.dagserver.infra.adapters.output.filesystem;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.linkedin.cytodynamics.nucleus.DelegateRelationshipBuilder;
import com.linkedin.cytodynamics.nucleus.IsolationLevel;
import com.linkedin.cytodynamics.nucleus.LoaderBuilder;
import com.linkedin.cytodynamics.nucleus.OriginRestriction;

import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;

@Component
public class FileSystemAdapter implements FileSystemOutputPort {

	@Value("${param.folderpath}")
	private String pathfolder;
	
	private static final String CLASSEXT = ".class";
	
	//para operador JDBC
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
			return loader.loadClass(name.replace("/", ".").replace(CLASSEXT, ""));
		} catch (ClassNotFoundException e) {
			throw new DomainException(e);
		}
	
	}
	@Override
	public Path getFolderPath() {
		return Paths.get(pathfolder);
		
	}

}
