package main.domain.services;

import java.util.ArrayList;

import org.springframework.stereotype.Service;
import main.application.ports.input.GetDefaultJobsUseCase;
import main.domain.core.DagExecutable;
import main.domain.dags.BackgroundSystemDag;
import main.domain.dags.EventSystemDag;

@Service
public class GetDefaultJobsService implements GetDefaultJobsUseCase {

	@Override
	public ArrayList<DagExecutable> getDefaultJobs() throws Exception {
		var defaultjobs = new ArrayList<DagExecutable>();
		defaultjobs.add(new BackgroundSystemDag());
		defaultjobs.add(new EventSystemDag());
		return defaultjobs;
	}

	
	
	
	

}