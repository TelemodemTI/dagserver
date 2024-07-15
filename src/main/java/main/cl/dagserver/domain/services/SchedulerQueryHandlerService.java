package main.cl.dagserver.domain.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.input.SchedulerQueryUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.AuthDTO;
import main.cl.dagserver.domain.model.ChannelDTO;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.DagDTO;
import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.types.Exceptions;




@Service
@ImportResource("classpath:properties-config.xml")
public class SchedulerQueryHandlerService extends BaseServiceComponent implements SchedulerQueryUseCase {
	
	private static final String INACTIVE = "INACTIVE";
	private static final String STATUS = "STATUS";
	private static final String RABBITMQ = "RABBIT_PROPS";
	private static final String REDIS_LISTENER = "REDIS_PROPS";
	private static final String KAFKA_CONSUMER = "KAFKA_CONSUMER";
	private static final String ACTIVEMQ_LISTENER = "ACTIVEMQ_LISTENER";
	
	
	@Value( "${param.git_hub.propkey}" )
	private String gitHubPropkey;
	
	@Value( "${param.rabbit.propkey}" )
	private String rabbitPropkey;
	
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
	@Value( "${param.kafka.propkey}" )
	private String kafkaPropkey;
	
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
	public List<ChannelDTO> getChannels(String token) throws DomainException {
		var claims = auth.untokenize(token);
	    validateAdminPermission(claims);
	    List<ChannelDTO> channels = new ArrayList<>();
	    channels.add(createChannel("SCHEDULER", "ACTIVE", "scheduler.png", Collections.emptyList()));
	    channels.add(createChannel("GRAPHQL", "ACTIVE", "graphql.png", Collections.emptyList()));
	    channels.add(createChannel("GITHUB_CHANNEL", getChannelStatus("GITHUB_WEBHOOK_PROPS"), "github.png", getChannelProps("GITHUB_WEBHOOK_PROPS")));
	    channels.add(createChannel(RABBITMQ, getChannelStatus(RABBITMQ), "rabbit.png", getChannelProps(RABBITMQ)));
	    channels.add(createChannel(REDIS_LISTENER, getChannelStatus(REDIS_LISTENER), "redis.png", getChannelProps(REDIS_LISTENER)));
	    channels.add(createChannel(KAFKA_CONSUMER, getChannelStatus(KAFKA_CONSUMER), "kafka.png", getChannelProps(KAFKA_CONSUMER)));
	    channels.add(createChannel(ACTIVEMQ_LISTENER, getChannelStatus(ACTIVEMQ_LISTENER), "activemq.png", getChannelProps(ACTIVEMQ_LISTENER)));
	    return channels;
	}
	
	

	private void validateAdminPermission(AuthDTO claims) throws DomainException {
	    if (!AccountType.ADMIN.equals(claims.getAccountType())) {
	        throw new DomainException(new Exception("unauthorized"));
	    }
	}

	private String getChannelStatus(String channelName) throws DomainException {
	    String status = INACTIVE;
	    List<PropertyParameterDTO> propsList = repository.getProperties(channelName);
	    
	    for (PropertyParameterDTO prop : propsList) {
	        if (prop.getName().equals(STATUS)) {
	            status = prop.getValue();
	            break;
	        }
	    }
	    return status;
	}

	private List<ChannelPropsDTO> getChannelProps(String channelName) throws DomainException {
	    List<ChannelPropsDTO> props = new ArrayList<>();
	    List<PropertyParameterDTO> propsList = repository.getProperties(channelName);
	    
	    for (PropertyParameterDTO prop : propsList) {
	        if (!prop.getName().equals(STATUS)) {
	            ChannelPropsDTO channelProp = new ChannelPropsDTO();
	            channelProp.setKey(prop.getName());
	            channelProp.setDescr(prop.getDescription());
	            channelProp.setValue(prop.getValue());
	            props.add(channelProp);
	        }
	    }
	    return props;
	}

	private ChannelDTO createChannel(String name, String status, String icon, List<ChannelPropsDTO> props) {
	    ChannelDTO channel = new ChannelDTO();
	    channel.setName(name);
	    channel.setStatus(status);
	    channel.setIcon(icon);
	    channel.setProps(props);
	    return channel;
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
	public List<Exceptions> getExceptions(String token) {
		auth.untokenize(token);
		List<Exceptions> newrv = new ArrayList<>();
		var exceptions = this.storage.listException();
		var keys = exceptions.keySet();
		for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
			String dt = iterator.next();
			Map<String,String> map = (Map<String,String>) exceptions.get(dt);
			Exceptions ex = new Exceptions();
			ex.setEventDt(dt);
			ex.setClassname(map.get("classname"));
			ex.setMethod(map.get("method"));
			ex.setStack(map.get("stacktrace"));
			newrv.add(ex);
		}
		return newrv;
	}
	
}
