package main.cl.dagserver.application.ports.output;

import java.util.Map;

import main.cl.dagserver.domain.core.ExceptionEventLog;

public interface ExceptionStorageUseCase {

	void add(ExceptionEventLog event);
	Map<String,Object> list();
	void remove(String eventDt);
}
