package main.cl.dagserver.domain.core;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.KeystoreOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.application.ports.output.StorageOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.infra.adapters.confs.QuartzConfig;

@Component
@ImportResource("classpath:properties-config.xml")
public class BaseServiceComponent {

	@Autowired
	protected FileSystemOutputPort fileSystem;
	
	@Autowired
	protected SchedulerRepositoryOutputPort repository;
	
	@Autowired 
	protected JarSchedulerOutputPort scanner;
	
	@Autowired
	protected CompilerOutputPort compiler;

	@Autowired
	protected StorageOutputPort storage;
	
	@Autowired
	protected AuthenticationOutputPort auth;
	
	@Autowired
	protected KeystoreOutputPort keystore;
	
	@Autowired
	protected QuartzConfig quartz;
	
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
	
	
	
}
