package main.application.ports.input;

import java.util.List;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;

public interface GetDefaultJobsUseCase {
	public List<DagExecutable> getDefaultJobs() throws DomainException;
}
