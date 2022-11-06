package server.application.handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import server.application.core.JarScheduler;
import server.application.core.TokenEngine;
import server.domain.entities.EventListener;
import server.domain.entities.Log;
import server.domain.entities.User;
import server.domain.repositories.SchedulerRepository;
import server.infra.quartz.QuartzConfig;


@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerHandler {
	
	@Value( "${param.jwt_secret}" )
	private String jwt_secret;

	@Value( "${param.jwt_signer}" )
	private String jwt_signer;
	
	@Value( "${param.jwt_subject}" )
	private String jwt_subject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwt_ttl;
	
	@Autowired
	SchedulerRepository repository;
	
	@Autowired
	QuartzConfig quartz;
	
	private static Logger log = Logger.getLogger(SchedulerHandler.class);
	private static String path = "C:\\exmple\\dagjava";
	
	public List<Map<String,Object>> listScheduledJobs() throws Exception {
		List<Map<String,Object>> realscheduled = quartz.listScheduled();
		var list = repository.listEventListeners();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			EventListener eventListener = (EventListener) iterator.next();
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
	public Map<String,List<Map<String,String>>> availableJobs() throws Exception {
		var scanner = new JarScheduler(this.path,quartz);
		var rv = scanner.getOperators();
		return rv;
	}


	public void scheduleDag(String token, String dagname,String jarname) throws Exception {
		Map<String,Object> claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		var scanner = new JarScheduler(this.path,quartz);
		scanner.scheduler(dagname,jarname);
	}
	public void unscheduleDag(String token,String dagname,String jarname) throws Exception {
		Map<String,Object> claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		var scanner = new JarScheduler(this.path,quartz);
		scanner.unschedule(dagname,jarname);
	}
	public List<Log> getLogs(String dagname) {
		return repository.getLogs(dagname);
	}

	public String login(String username,String pwdhash) throws Exception {
		List<User> list = repository.findUser(username);
		if(list.size() > 0) {
			User user = list.get(0);
			String hash = TokenEngine.sha256(pwdhash);
			
			if(hash.equals(user.getPwdhash())) {
				Map<String,String> claims = new HashMap<String,String>();
				claims.put("username", username);
				claims.put("userid", user.getId().toString());
				String token = TokenEngine.tokenize(jwt_secret, jwt_signer, jwt_subject, jwt_ttl, claims);
				return token;
			} else {
				return "";
			}
		} else {
			return "";
		}
		
	}
}
