package main.domain.services;

import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.input.SchedulerMutationUseCase;
import main.application.ports.output.JarSchedulerOutputPort;
import main.domain.repositories.SchedulerRepository;
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
	SchedulerRepository repository;
	
	@Autowired 
	JarSchedulerOutputPort scanner;
		
	private static Logger log = Logger.getLogger(SchedulerMutationHandlerService.class);
	
	
	
	@Override
	public void scheduleDag(String token, String dagname,String jarname) throws Exception {
		Map<String,Object> claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().scheduler(dagname,jarname);
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws Exception {
		Map<String,Object> claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().unschedule(dagname,jarname);
	}
}
