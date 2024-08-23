package main.cl.dagserver.domain.services;

import java.net.URI;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;

@Service
public class InternalOperatorService extends BaseServiceComponent implements InternalOperatorUseCase{

	@Override
	public void deleteLogsBy(Date rolldate) {
		this.repository.deleteLogsBy(rolldate);
	}

	@Override
	public void setMetadata(String hostname, String name) {
		this.repository.setMetadata(hostname, name);
	}

	@Override
	public Class<?> loadFromOperatorJar(String name, List<URI> list) throws DomainException {
		return this.fileSystem.loadFromOperatorJar(name, list);	
	}

	@Override
	public void deleteXCOM(Date time) throws DomainException {
		this.scanner.deleteXCOM(time);
	}

	@Override
	public ClassLoader getClassLoader(List<URI> list) throws DomainException {
		return this.fileSystem.getClassLoader(list);
	}

	@Override
	public Path getFolderPath() {
		return this.fileSystem.getFolderPath();
	}

}
