package main.application.ports.input;

import java.util.List;
import java.util.Map;

import main.domain.model.AgentDTO;
import main.domain.model.DagDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.UncompiledDTO;


public interface SchedulerQueryUseCase {

	List<Map<String, Object>> listScheduledJobs() throws Exception;

	Map<String, List<Map<String, String>>> availableJobs() throws Exception;

	List<LogDTO> getLogs(String dagname);

	List<DagDTO> getDagDetail(String jarname) throws Exception;

	List<PropertyDTO> properties()  throws Exception;

	List<AgentDTO> agents();
	
	List<UncompiledDTO> getUncompileds(String token) throws Exception;
}