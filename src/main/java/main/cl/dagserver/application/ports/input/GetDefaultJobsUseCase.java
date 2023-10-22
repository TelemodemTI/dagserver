package main.cl.dagserver.application.ports.input;

import java.util.List;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.exceptions.DomainException;

public interface GetDefaultJobsUseCase {
	public List<DagExecutable> getDefaultJobs() throws DomainException;
}
