package main.application.ports.input;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;

import main.domain.exceptions.DomainException;
import main.domain.model.AgentDTO;
import main.domain.model.DagDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;


public interface SchedulerQueryUseCase {

	List<Map<String, Object>> listScheduledJobs() throws DomainException;
	Map<String, List<Map<String, String>>> availableJobs() throws DomainException;
	List<LogDTO> getLogs(String dagname)  throws DomainException;
	List<DagDTO> getDagDetail(String jarname) throws DomainException;
	List<PropertyDTO> properties()  throws DomainException;
	List<AgentDTO> agents();
	List<UncompiledDTO> getUncompileds(String token) throws DomainException;
	JSONArray operators() throws DomainException;
	List<UserDTO> credentials(String token) throws DomainException;
	String getIcons(String type) throws DomainException;
}