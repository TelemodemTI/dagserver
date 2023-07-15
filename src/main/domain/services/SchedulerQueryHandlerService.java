package main.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import main.application.ports.input.SchedulerQueryUseCase;
import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;
import main.domain.model.AgentDTO;
import main.domain.model.DagDTO;
import main.domain.model.EventListenerDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.PropertyParameterDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;




@Service
@ImportResource("classpath:properties-config.xml")
public class SchedulerQueryHandlerService implements SchedulerQueryUseCase {
	
	@Value( "${param.jwt_secret}" )
	private String jwt_secret;

	@Value( "${param.jwt_signer}" )
	private String jwt_signer;
	
	@Value( "${param.jwt_subject}" )
	private String jwt_subject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwt_ttl;
	
	@Value( "${param.folderpath}" )
	private String path;
	
	@Autowired
	SchedulerRepositoryOutputPort repository;
	
	@Autowired 
	JarSchedulerOutputPort scanner;
	
	@Autowired
	CompilerOutputPort compiler;

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SchedulerQueryHandlerService.class);
	
	@Override
	public List<Map<String,Object>> listScheduledJobs() throws Exception {
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
	public Map<String,List<Map<String,String>>> availableJobs() throws Exception {
		var rv = scanner.init().getOperators();
		return rv;
	}
	@Override
	public List<LogDTO> getLogs(String dagname) throws Exception {
		List<LogDTO> newrv = new ArrayList<>();
		var list = repository.getLogs(dagname);
		for (Iterator<LogDTO> iterator = list.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			JSONObject xcom = repository.readXcom(logDTO.getOutputxcom());
			logDTO.setOutputxcom(xcom.toString());
			newrv.add(logDTO);
		}
		return newrv;
	}
	public List<DagDTO> getDagDetail(String jarname) throws Exception {
		return scanner.init().getDagDetail(jarname);
	}
	@Override
	public List<PropertyDTO> properties() throws Exception {
		List<PropertyDTO> res = new ArrayList<PropertyDTO>();
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
	public List<UncompiledDTO> getUncompileds(String token) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer).get("claims");
		return repository.getUncompileds();
	}
	@Override
	public JSONArray operators() throws Exception {
		return compiler.operators();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<UserDTO> credentials(String token) throws Exception {
		Map<String,String> claims = (Map<String, String>) TokenEngine.untokenize(token, jwt_secret, jwt_signer).get("claims");
		if(claims.get("typeAccount").equals("ADMIN")) {
			return repository.getUsers();	
		} else {
			return new ArrayList<UserDTO>();
		}
	}
	@Override
	public String getIcons(String type) throws Exception {
		return scanner.getIcons(type);
	}
}
