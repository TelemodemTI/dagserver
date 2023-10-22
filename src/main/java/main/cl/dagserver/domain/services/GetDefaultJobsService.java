package main.cl.dagserver.domain.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import main.cl.dagserver.application.ports.input.GetDefaultJobsUseCase;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.dags.BackgroundSystemDag;
import main.cl.dagserver.domain.dags.EventSystemDag;
import main.cl.dagserver.domain.exceptions.DomainException;

@Service
public class GetDefaultJobsService implements GetDefaultJobsUseCase {

	@Override
	public ArrayList<DagExecutable> getDefaultJobs() throws DomainException {
		try {
			var defaultjobs = new ArrayList<DagExecutable>();
			defaultjobs.add(new BackgroundSystemDag());
			defaultjobs.add(new EventSystemDag());
			return defaultjobs;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}

	
	
	
	

}