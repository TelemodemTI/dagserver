package main.domain.core;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.model.PropertyParameterDTO;

@Component
@ImportResource("classpath:properties-config.xml")
public class BaseServiceComponent {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(BaseServiceComponent.class);
	
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
	
	@Autowired
	protected TokenEngine tokenEngine;
	
	protected void trigggerEvent(String artifact, String eventType)  {
		try {
			var propertyList = repository.getProperties(artifact);
			String dagname = "";
			String jarname = "";
			for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
				PropertyParameterDTO propertyParameterDTO = iterator.next();
				if(propertyParameterDTO.getName().equals("dagname")) {
					dagname = propertyParameterDTO.getValue();
				}
				if(propertyParameterDTO.getName().equals("jarname")) {
					jarname = propertyParameterDTO.getValue();
				}
			}
			if(!dagname.isEmpty() && !jarname.isEmpty()) {
				scanner.init().execute(jarname, dagname,eventType);	
			}	
		} catch (Exception e) {
			log.error(e);
		}
	}
}
