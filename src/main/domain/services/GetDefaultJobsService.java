package main.domain.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import main.application.ports.input.GetDefaultJobsUseCase;
import main.domain.core.DagExecutable;
import main.domain.dags.BackgroundSystemDag;
import main.domain.dags.EventSystemDag;
import main.domain.exceptions.DomainException;

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
			throw new DomainException(e.getMessage());
		}
		
	}

	
	
	
	

}