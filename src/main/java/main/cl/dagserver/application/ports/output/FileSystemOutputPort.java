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
	public Path getPath(String inputPath);
	public DirectoryEntryDTO getContents() throws DomainException;
	public void upload(Path tempFile, String uploadPath, String realname) throws DomainException;
	public void createFolder(String foldername) throws DomainException;
	public void delete(String folder, String file) throws DomainException;
	public Path getFilePath(String folderPath, String filename);
	public void copyFile(String filename, String copyname) throws DomainException;
	public void moveFile(String folder,String filename, String newpath) throws DomainException;
}
