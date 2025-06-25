package main.cl.dagserver.domain.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.input.SchedulerQueryUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.domain.model.ExceptionsDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;



@Service
@ImportResource("classpath:properties-config.xml")
public class SchedulerQueryHandlerService extends BaseServiceComponent implements SchedulerQueryUseCase {


	@Override
	public List<Map<String,Object>> listScheduledJobs() throws DomainException {
		List<Map<String,Object>> realscheduled = scanner.listScheduled();
		var list = repository.listEventListeners();
		for (Iterator<EventListenerDTO> iterator = list.iterator(); iterator.hasNext();) {
			EventListenerDTO eventListener = iterator.next();
			Map<String,Object> mapa = new HashMap<>();
			mapa.put("jobname",eventListener.getListenerName());
			mapa.put("jobgroup",eventListener.getGroupName());
			mapa.put("nextFireAt",null);
			var eventTrigger = eventListener.getOnEnd().equals("") ? eventListener.getOnStart() : eventListener.getOnEnd();
			mapa.put("eventTrigger", eventTrigger);
			realscheduled.add(mapa);
		}
		return realscheduled;
	}
	@Override
	public Map<String,List<Map<String,String>>> availableJobs() throws DomainException {
		return scanner.init().getOperators();
	}
	@Override
	public List<LogDTO> getLogs(String dagname) throws DomainException {
		List<LogDTO> newrv = new ArrayList<>();
		var list = repository.getLogs(dagname);
		for (Iterator<LogDTO> iterator = list.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			Map<String, DataFrame> xcom = repository.readXcom(logDTO.getOutputxcom());
			logDTO.setXcom(xcom);
			newrv.add(logDTO);
		}
		return newrv;
	}
	public List<DagDTO> getDagDetail(String jarname) throws DomainException {
		return scanner.init().getDagDetail(jarname);
	}
	@Override
	public List<PropertyDTO> properties() throws DomainException {
		List<PropertyDTO> res = new ArrayList<>();
		var sollection = repository.getProperties(null);
		for (Iterator<PropertyParameterDTO> iterator = sollection.iterator(); iterator.hasNext();) {
			PropertyParameterDTO type = iterator.next();
			PropertyDTO newitem = new PropertyDTO();
			newitem.setDescription(type.getDescription());
			newitem.setGroup(type.getGroup());
			newitem.setName(type.getName());
			newitem.setValue(type.getValue());
			res.add(newitem);
		}
		return res;
	}
	public List<AgentDTO> agents(){
		return repository.getAgents();
	}
	@Override
	public List<UncompiledDTO> getUncompileds(String token) throws DomainException {
		try {
			auth.untokenize(token);
			return repository.getUncompileds();	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public JSONArray operators() throws DomainException {
		return compiler.operators();
	}
	@Override
	public List<UserDTO> credentials(String token) throws DomainException {
		try {
			var map = auth.untokenize(token);
			if(map.getAccountType().equals(AccountType.ADMIN)) {
				return repository.getUsers();	
			} else {
				return new ArrayList<>();
			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public String getIcons(String type) throws DomainException {
		return scanner.getIcons(type);
	}
	@Override
	public List<List<String>> getDependencies(String jarname, String dagname) throws DomainException {
		var list = repository.listEventListeners();
		List<String> onEnd = new ArrayList<>();
		List<String> onStart = new ArrayList<>();
		for (Iterator<EventListenerDTO> iterator = list.iterator(); iterator.hasNext();) {
			EventListenerDTO eventListenerDTO = iterator.next();
			if(eventListenerDTO.getOnEnd().equals(dagname)) {			
				onEnd.add(this.getCanonicalname(eventListenerDTO.getListenerName()));
			}
			if(eventListenerDTO.getOnStart().equals(dagname)) {
				onStart.add(this.getCanonicalname(eventListenerDTO.getListenerName()));
			}
		}
		return Arrays.asList(onStart,onEnd);
	}
	private String getCanonicalname(String dagname) throws DomainException {
		var listop = scanner.init().getOperators();
		String returnv = "";
		for (Entry<String, List<Map<String, String>>> entrada : listop.entrySet()) {
            String clave = entrada.getKey();
            var dags = entrada.getValue();
            for (Iterator<Map<String, String>> iterator = dags.iterator(); iterator.hasNext();) {
				Map<String, String> map = iterator.next();
				String namev = map.get("dagname");
				if(namev.equals(dagname)) {
					returnv = clave;
					break;
				}
			}
        }
		returnv = (returnv.isBlank())?"SYSTEM":returnv;
		return returnv+"."+dagname;
	}

	@Override
	public String exportUncompiled(String token, Integer uncompiled) throws DomainException {
		auth.untokenize(token);
		return repository.getUncompiledBin(uncompiled);
	}
	@Override
	public List<LogDTO> getLastLogs() throws DomainException {
		List<LogDTO> newrv = new ArrayList<>();
		var list = repository.getLastLogs();
		for (Iterator<LogDTO> iterator = list.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			Map<String, DataFrame> xcom = repository.readXcom(logDTO.getOutputxcom());
			logDTO.setXcom(xcom);
			newrv.add(logDTO);
		}
		return newrv;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<ExceptionsDTO> getExceptions(String token) throws DomainException {
		auth.untokenize(token);
		List<ExceptionsDTO> newrv = new ArrayList<>();
		var exceptions = this.storage.listException();
		var keys = exceptions.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String dt = iterator.next();
			Map<String,String> map = (Map<String,String>) exceptions.get(dt);
			ExceptionsDTO ex = new ExceptionsDTO();
			ex.setEventDt(dt);
			ex.setClassname(map.get("classname"));
			ex.setMethod(map.get("method"));
			ex.setStack(map.get("stacktrace"));
			newrv.add(ex);
		}
		return newrv;
	}
	@Override
	public DirectoryEntryDTO mounted(String token) throws DomainException {
		auth.untokenize(token);
		return this.fileSystem.getContents();
	}
	@Override
	public List<KeystoreEntryDTO> getKeystoreEntries(String token) throws DomainException {
		auth.untokenize(token);
		return this.keystore.getEntries();
	}
	
}
