package main.application.ports.input;

import java.util.List;
import java.util.Map;

import main.domain.entities.Log;
import main.domain.messages.DagDTO;
import main.domain.types.Agent;
import main.domain.types.Property;
import main.domain.types.Uncompiled;

public interface SchedulerQueryUseCase {

	List<Map<String, Object>> listScheduledJobs() throws Exception;

	Map<String, List<Map<String, String>>> availableJobs() throws Exception;

	List<Log> getLogs(String dagname);

	List<DagDTO> getDagDetail(String jarname) throws Exception;

	List<Property> properties()  throws Exception;

	List<Agent> agents();
	
	List<Uncompiled> getUncompileds(String token) throws Exception;
}