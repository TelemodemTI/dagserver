package main.cl.dagserver.application.ports.input;

import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.domain.model.ExceptionsDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;

public interface SchedulerQueryUseCase {

	List<Map<String, Object>> listScheduledJobs() throws DomainException;
	Map<String, List<Map<String, String>>> availableJobs() throws DomainException;
	List<LogDTO> getLogs(String dagname) throws DomainException;
	List<DagDTO> getDagDetail(String jarname) throws DomainException;
	List<PropertyDTO> properties()  throws DomainException;
	List<AgentDTO> agents();
	List<UncompiledDTO> getUncompileds(String token) throws DomainException;
	JSONArray operators() throws DomainException;
	List<UserDTO> credentials(String token) throws DomainException;
	String getIcons(String type) throws DomainException;
	List<List<String>> getDependencies(String jarname,String dagname) throws DomainException;
	
	String exportUncompiled(String token, Integer uncompiled) throws DomainException;
	List<LogDTO> getLastLogs() throws DomainException;
	List<ExceptionsDTO> getExceptions(String token) throws DomainException;
	List<ExceptionsDTO> getExceptions(String token,String evalkey) throws DomainException;
	DirectoryEntryDTO mounted(String token) throws DomainException;
	List<KeystoreEntryDTO> getKeystoreEntries(String token) throws DomainException;
}