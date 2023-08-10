package main.application.ports.output;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;
import main.domain.enums.OperatorStatus;
import main.domain.exceptions.DomainException;
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
	public void setLog(String dagname,String value,String xcom, Map<String, OperatorStatus> status);
	public void deleteLogsBy(Date rolldate);
	public List<UserDTO> findUser(String username);
	public List<PropertyParameterDTO> getProperties(String groupname) throws DomainException;
	public Properties getPropertiesFromDb(String groupname) throws DomainException;
	public void setProperty(String name, String description, String value,String group); 
	public void delProperty(String name,String group);
	public void setMetadata(String hostname,String name); 
	public List<AgentDTO> getAgents();
	public void insertIfNotExists(String jarname,String propertiesFile, Properties properties);
	public void addUncompiled(String name, JSONObject json);
	public void updateUncompiled(Integer uncompiled,JSONObject json);
	public List<UncompiledDTO> getUncompileds();
	public String getUncompiledBin(Integer uncompiled);
	public void deleteUncompiled(Integer uncompiled);
	public void createParams(String jarname, String bin) throws DomainException;
	public String createInternalStatus(JSONObject data) throws DomainException;
	public JSONObject readXcom(String locatedAt) throws DomainException;
	public void delGroupProperty(String group);
	public List<UserDTO> getUsers();
	public void createAccount(String username, String accountType, String pwdHash);
	public void delAccount(String username);
	public void updateParams(String idope, String typeope, String jarname, String bin);
}
