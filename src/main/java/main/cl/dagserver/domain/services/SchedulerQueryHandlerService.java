package main.cl.dagserver.domain.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import main.cl.dagserver.application.ports.input.SchedulerQueryUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.ChannelDTO;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;




@Service
@ImportResource("classpath:properties-config.xml")
public class SchedulerQueryHandlerService extends BaseServiceComponent implements SchedulerQueryUseCase {
	
	private static final String INACTIVE = "INACTIVE";
	private static final String STATUS = "STATUS";
	
	@Value( "${param.git_hub.propkey}" )
	private String gitHubPropkey;
	
	@Value( "${param.rabbit.propkey}" )
	private String rabbitPropkey;
	
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
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
			JSONObject xcom = repository.readXcom(logDTO.getOutputxcom());
			logDTO.setOutputxcom(xcom.toString());
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
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			return repository.getUncompileds();	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public JSONArray operators() throws DomainException {
		return compiler.operators();
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<UserDTO> credentials(String token) throws DomainException {
		try {
			var map = tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			Map<String,String> claims = (Map<String, String>) map.get("claims");
			if(claims.get("typeAccount").equals("ADMIN")) {
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
	@SuppressWarnings("unchecked")
	public List<ChannelDTO> getChannels(String token) throws DomainException {
		Map<String,String> claims = (Map<String, String>) tokenEngine.untokenize(token, jwtSecret, jwtSigner).get("claims");
		if(!claims.get("typeAccount").equals("ADMIN")) {
			throw new DomainException(new Exception("unauthorized"));
		}
		List<ChannelPropsDTO> props = new ArrayList<>();
		String githubStatus = INACTIVE;
		var propertyList = repository.getProperties(gitHubPropkey);
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getName().equals(STATUS)){
				githubStatus = propertyParameterDTO.getValue();
			} else {
				ChannelPropsDTO prop1 = new ChannelPropsDTO();
				prop1.setKey(propertyParameterDTO.getName());
				prop1.setDescr(propertyParameterDTO.getDescription());
				prop1.setValue(propertyParameterDTO.getValue());
				props.add(prop1);
			}
		}
		ChannelDTO github = new ChannelDTO();
		github.setName("GITHUB_CHANNEL");
		github.setStatus(githubStatus);
		github.setProps(props);
		ChannelDTO scheduler = new ChannelDTO();
		scheduler.setName("SCHEDULER");
		scheduler.setStatus("ACTIVE");
		
		
		String rabbitStatus = INACTIVE;
		List<ChannelPropsDTO> rabbitprops = new ArrayList<>();
		var rabbitPropsList = repository.getProperties(rabbitPropkey);
		for (Iterator<PropertyParameterDTO> iterator = rabbitPropsList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getName().equals(STATUS)){
				rabbitStatus = propertyParameterDTO.getValue();
			} else {
				ChannelPropsDTO prop1 = new ChannelPropsDTO();
				prop1.setKey(propertyParameterDTO.getName());
				prop1.setDescr(propertyParameterDTO.getDescription());
				prop1.setValue(propertyParameterDTO.getValue());
				rabbitprops.add(prop1);
			}
		}
		
		
		ChannelDTO rabbit = new ChannelDTO();
		rabbit.setName("RABBITMQ");
		rabbit.setStatus(rabbitStatus);
		rabbit.setProps(rabbitprops);
		
		String redisStatus = INACTIVE;
		List<ChannelPropsDTO> redisprops = new ArrayList<>();
		var redisPropsList = repository.getProperties(rabbitPropkey);
		for (Iterator<PropertyParameterDTO> iterator = redisPropsList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getName().equals(STATUS)){
				redisStatus = propertyParameterDTO.getValue();
			} else {
				ChannelPropsDTO prop1 = new ChannelPropsDTO();
				prop1.setKey(propertyParameterDTO.getName());
				prop1.setDescr(propertyParameterDTO.getDescription());
				prop1.setValue(propertyParameterDTO.getValue());
				redisprops.add(prop1);
			}
		}
		
		
		ChannelDTO redis = new ChannelDTO();
		redis.setName("REDIS_LISTENER");
		redis.setStatus(redisStatus);
		redis.setProps(redisprops);
		
		List<ChannelDTO> list = new ArrayList<>();
		list.add(scheduler);
		list.add(github);
		list.add(rabbit);
		list.add(redis);
		return list;
	}
	@Override
	public String exportUncompiled(String token, Integer uncompiled) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		return repository.getUncompiledBin(uncompiled);
	}
	@Override
	public List<LogDTO> getLastLogs() throws DomainException {
		List<LogDTO> newrv = new ArrayList<>();
		var list = repository.getLastLogs();
		for (Iterator<LogDTO> iterator = list.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			JSONObject xcom = repository.readXcom(logDTO.getOutputxcom());
			logDTO.setOutputxcom(xcom.toString());
			newrv.add(logDTO);
		}
		return newrv;
	}
}
