package main.domain.services;


import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.input.SchedulerMutationUseCase;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;




@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerMutationHandlerService implements SchedulerMutationUseCase {
	
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
		
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SchedulerMutationHandlerService.class);
	
	
	
	@Override
	public void scheduleDag(String token, String dagname,String jarname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().scheduler(dagname,jarname);
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().unschedule(dagname,jarname);
	}
	@Override
	public void createProperty(String token, String name, String description, String value,String group) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.setProperty(name,description,value,group);
	}
	@Override
	public void deleteProperty(String token, String name,String group) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.delProperty(name,group);
	}
	@Override
	public void execute(String token, String jarname, String dagname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().execute(jarname, dagname);
	}
	@Override
	public void saveUncompiled(String token, JSONObject json) throws Exception {
		Map<String,Object> claims = (Map<String, Object>) TokenEngine.untokenize(token, jwt_secret, jwt_signer).get("claims");
		repository.addUncompiled(claims.get("username").toString(), json.getString("jarname"),json,Integer.parseInt(claims.get("userid").toString()));
	}
}
