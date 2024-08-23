package main.cl.dagserver.application.ports.input;

import java.net.URI;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface InternalOperatorUseCase {
	public void deleteLogsBy(Date rolldate);
	public void setMetadata(String hostname,String name);
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException;
	public void deleteXCOM(Date time)  throws DomainException;
	public ClassLoader getClassLoader(List<URI> list) throws DomainException;
	public Path getFolderPath();
}
