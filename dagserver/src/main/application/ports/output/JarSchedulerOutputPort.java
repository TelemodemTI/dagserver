package main.application.ports.output;
import java.util.List;
import java.util.Map;

import main.infra.adapters.output.JarSchedulerAdapter;

public interface JarSchedulerOutputPort {

	public JarSchedulerAdapter init () throws Exception;
	
	Map<String, List<Map<String, String>>> getOperators();

	void scheduler(String dagname, String jarname) throws Exception;

	void unschedule(String dagname, String jarname) throws Exception;

}