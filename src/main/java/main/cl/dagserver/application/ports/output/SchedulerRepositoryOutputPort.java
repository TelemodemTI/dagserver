package main.cl.dagserver.application.ports.output;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;

import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;

public interface SchedulerRepositoryOutputPort {
	public void addEventListener(String name,String onstart,String onend,String groupname);
	public void removeListener(String name);
	public List<EventListenerDTO> listEventListeners();
	public List<EventListenerDTO> getEventListeners(String listenerName);
	public List<LogDTO> getLogs(String dagname);
	public LogDTO getLog(Integer logid);
	public void setLog(Map<String,String> parmdata, Map<String, OperatorStatus> status, List<String> timestamps);
	public void deleteLogsBy(Date rolldate);
	public List<UserDTO> findUser(String username);
	public List<PropertyParameterDTO> getProperties(String groupname) throws DomainException;
	public Properties getPropertiesFromDb(String groupname) throws DomainException;
	public void setProperty(String name, String description, String value,String group); 
	public void delProperty(String name,String group);
	public void setMetadata(String hostname,String name); 
	public List<AgentDTO> getAgents();
	public void insertIfNotExists(String jarname,String propertiesFile, Properties properties);
	public void addUncompiled(String name, JSONObject json) throws DomainException;
	public void updateUncompiled(Integer uncompiled,JSONObject json) throws DomainException;
	public List<UncompiledDTO> getUncompileds();
	public String getUncompiledBin(Integer uncompiled);
	public void deleteUncompiled(Integer uncompiled);
	public List<String> createParams(String jarname, String bin) throws DomainException;
	public String createInternalStatus(JSONObject data) throws DomainException;
	public JSONObject readXcom(String locatedAt) throws DomainException;
	public void delGroupProperty(String group);
	public List<UserDTO> getUsers();
	public void createAccount(String username, String accountType, String pwdHash);
	public void delAccount(String username);
	public void updateParams(String idope, String typeope, String jarname, String bin);
	public void updateprop(String group, String key, String value);
	public void deleteLog(Integer logid);
	public void deleteAllLogs(String dagname);
	public void renameUncompiled(Integer uncompiled, String newname);
	public List<LogDTO> getLastLogs();
}
