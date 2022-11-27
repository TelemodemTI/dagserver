package main.application.ports.input;

import java.util.List;
import java.util.Map;

import main.domain.entities.Log;

public interface SchedulerQueryUseCase {

	List<Map<String, Object>> listScheduledJobs() throws Exception;

	Map<String, List<Map<String, String>>> availableJobs() throws Exception;

	List<Log> getLogs(String dagname);

	

}