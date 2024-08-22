package main.cl.dagserver.domain.services;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.linkedin.cytodynamics.nucleus.DelegateRelationshipBuilder;
import com.linkedin.cytodynamics.nucleus.IsolationLevel;
import com.linkedin.cytodynamics.nucleus.LoaderBuilder;
import com.linkedin.cytodynamics.nucleus.OriginRestriction;
import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;

@Service
public class InternalOperatorService extends BaseServiceComponent implements InternalOperatorUseCase{

	private static final String CLASSEXT = ".class";
	
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
	public void deleteXCOM(Date time) throws DomainException {
		this.scanner.deleteXCOM(time);
	}

}
