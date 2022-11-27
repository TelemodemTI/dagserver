package main.application.ports.input;

import java.util.ArrayList;

import main.domain.core.DagExecutable;


public interface GetDefaultJobsUseCase {
	public ArrayList<DagExecutable> getDefaultJobs() throws Exception;
}
