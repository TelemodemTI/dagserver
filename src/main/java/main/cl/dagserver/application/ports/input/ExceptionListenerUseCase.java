package main.cl.dagserver.application.ports.input;

import main.cl.dagserver.domain.core.ExceptionEventLog;

public interface ExceptionListenerUseCase {

	void registerException(ExceptionEventLog event);

}
