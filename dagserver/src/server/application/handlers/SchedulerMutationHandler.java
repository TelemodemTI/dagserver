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
public class SchedulerMutationHandler {
	
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
	QuartzConfig quartz;
	
	private static Logger log = Logger.getLogger(SchedulerMutationHandler.class);
	
	
	
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
}
