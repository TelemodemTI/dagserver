package main.cl.dagserver.domain.services;

import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.ActiveMQChannelUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
public class ActiveMQService extends BaseServiceComponent implements ActiveMQChannelUseCase {
	@Value( "${param.activemq.propkey}" )
	private String activemqPropkey;
	
	
	@Override
	public Properties getActiveMQChannelProperties() throws DomainException {
		return this.getChannelProperties(activemqPropkey, "activemq_consumer_listener");
	}

	@Override
	public void raiseEvent(String channel, String message) throws DomainException {
		this.trigggerEvent(channel, "ACTIVEMQ_EVENT",message);
	}

	@Override
	public Properties getActiveMQListeners() throws DomainException {
		var propertyList = repository.getProperties(activemqPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getValue().equals("activemq_consumer_listener")) {
				props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());	
			}
		}
		return props;
	}

}
