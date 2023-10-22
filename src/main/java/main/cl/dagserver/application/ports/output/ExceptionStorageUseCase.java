package main.cl.dagserver.application.ports.output;

import main.cl.dagserver.domain.core.ExceptionEventLog;

public interface ExceptionStorageUseCase {

	void add(ExceptionEventLog event);

}
