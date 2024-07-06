package main.cl.dagserver.domain.core;
import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.ExceptionStorageUseCase;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

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
	
	@Autowired
	protected ExceptionStorageUseCase excstorage;
	
	@Autowired
	protected TokenEngine tokenEngine;
	
	protected void trigggerEvent(String artifact, String eventType, String data) throws DomainException  {
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
				scanner.init().execute(jarname, dagname,eventType,data);	
		}	
	}
	
	
	protected Properties getChannelProperties(String propkey,String value) throws DomainException {
		var propertyList = repository.getProperties(propkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(!propertyParameterDTO.getValue().equals(value)) {
				props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());	
			}
		}
		return props;
	}
}
