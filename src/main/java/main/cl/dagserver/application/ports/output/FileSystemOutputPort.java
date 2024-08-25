package main.cl.dagserver.application.ports.output;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;

public interface FileSystemOutputPort {
	public ClassLoader getClassLoader(List<URI> list);
	public Class<?> loadFromJar(Path jarFile,String name) throws DomainException;
	public InputStream loadResourceFromJar(Path jarFile, String resource);
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException;
	public Path getFolderPath();
	public Path getFolderPath(String jarname);
	public Path getJDBCDriversPath(String inputPath);
	public DirectoryEntryDTO getContents() throws DomainException;
}
