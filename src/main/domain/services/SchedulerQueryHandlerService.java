package main.domain.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import main.application.ports.input.SchedulerQueryUseCase;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.QuartzOutputPort;
import main.domain.core.TokenEngine;
import main.domain.entities.EventListener;
import main.domain.entities.Log;
import main.domain.entities.User;
import main.domain.repositories.SchedulerRepository;



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
	
	@Autowired
	QuartzOutputPort quartz;
	
	private static Logger log = Logger.getLogger(SchedulerQueryHandlerService.class);
	
	@Override
	public List<Map<String,Object>> listScheduledJobs() throws Exception {
		List<Map<String,Object>> realscheduled = quartz.listScheduled();
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
	
}
