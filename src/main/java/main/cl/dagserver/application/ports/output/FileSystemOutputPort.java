package main.cl.dagserver.application.ports.output;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface FileSystemOutputPort {
	public ClassLoader getClassLoader(List<URI> list);
	public Class<?> loadFromJar(File jarFile,String name) throws DomainException;
	public InputStream loadResourceFromJar(File jarFile, String resource);
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException;
	public Path getFolderPath();
}
