package main.application.ports.output;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

import main.domain.enums.OperatorStatus;
import main.domain.model.AgentDTO;
import main.domain.model.EventListenerDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyParameterDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;

public interface SchedulerRepositoryOutputPort {
	public void addEventListener(String name,String onstart,String onend,String groupname);
	public void removeListener(String name);
	public List<EventListenerDTO> listEventListeners();
	public List<EventListenerDTO> getEventListeners(String listenerName);
	public List<LogDTO> getLogs(String dagname);
	public LogDTO getLog(Integer logid);
	public void setLog(String dagname,String value,Map<String,Object> xcom, Map<String, OperatorStatus> status);
	public void deleteLogsBy(Date rolldate);
	public List<UserDTO> findUser(String username);
	public List<PropertyParameterDTO> getProperties(String groupname) throws Exception;
	public Properties getPropertiesFromDb(String groupname) throws Exception;
	public void setProperty(String name, String description, String value,String group); 
	public void delProperty(String name,String group);
	public void setMetadata(String hostname,String name); 
	public List<AgentDTO> getAgents();
	public void insertIfNotExists(String jarname,String propertiesFile, Properties properties);
	public void addUncompiled(String name, JSONObject json, Integer userid);
	public void updateUncompiled(Integer uncompiled,JSONObject json);
	public List<UncompiledDTO> getUncompileds(int parseInt);
	public String getUncompiledBin(Integer uncompiled);
}
