package main.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.application.ports.input.SchedulerQueryUseCase;
import main.application.ports.output.JarSchedulerOutputPort;
import main.domain.entities.EventListener;
import main.domain.entities.Log;
import main.domain.entities.PropertyParameter;
import main.domain.messages.DagDTO;
import main.domain.repositories.SchedulerRepository;
import main.domain.types.Agent;
import main.domain.types.Property;



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
	SchedulerRepository repository;
	
	@Autowired 
	JarSchedulerOutputPort scanner;
	

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SchedulerQueryHandlerService.class);
	
	@Override
	public List<Map<String,Object>> listScheduledJobs() throws Exception {
		List<Map<String,Object>> realscheduled = scanner.listScheduled();
		var list = repository.listEventListeners();
		for (Iterator<EventListener> iterator = list.iterator(); iterator.hasNext();) {
			EventListener eventListener = iterator.next();
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
	public List<Log> getLogs(String dagname) {
		return repository.getLogs(dagname);
	}
	public List<DagDTO> getDagDetail(String jarname) throws Exception {
		return scanner.init().getDagDetail(jarname);
	}
	@Override
	public List<Property> properties() throws Exception {
		List<Property> res = new ArrayList<Property>();
		var sollection = repository.getProperties(null);
		for (Iterator<PropertyParameter> iterator = sollection.iterator(); iterator.hasNext();) {
			PropertyParameter type = iterator.next();
			Property newitem = new Property();
			newitem.setDescription(type.getDescription());
			newitem.setGroup(type.getGroup());
			newitem.setName(type.getName());
			res.add(newitem);
		}
		return res;
	}
	public List<Agent> agents(){
		return repository.getAgents();
	}
}
