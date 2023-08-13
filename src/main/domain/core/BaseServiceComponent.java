package main.domain.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;

@Component
@ImportResource("classpath:properties-config.xml")
public class BaseServiceComponent {

	@Value( "${param.jwt_secret}" )
	protected String jwtSecret;
	@Value( "${param.jwt_signer}" )
	protected String jwtSigner;
	
	@Value( "${param.jwt_subject}" )
	protected String jwtSubject;
	
	@Value( "${param.jwt_ttl}" )
	protected Integer jwtTtl;
	
	@Value( "${param.folderpath}" )
	protected String path;
	
	@Autowired
	protected SchedulerRepositoryOutputPort repository;
	
	@Autowired 
	protected JarSchedulerOutputPort scanner;
	
	@Autowired
	protected CompilerOutputPort compiler;
	
}
