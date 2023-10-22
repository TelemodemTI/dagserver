package main.cl.dagserver.domain.services;

import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.RabbitChannelUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
@ImportResource("classpath:properties-config.xml")
public class RabbitChannelService extends BaseServiceComponent implements RabbitChannelUseCase {

	@Value( "${param.rabbit.propkey}" )
	private String rabbitPropkey;
	
	@Override
	public Properties getRabbitChannelProperties() throws DomainException {
		var propertyList = repository.getProperties(rabbitPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());
		}
		return props;
	}

	@Override
	public void raiseEvent(String bodyStr, String queue, String routingKey, String contentType) throws DomainException  {
		this.trigggerEvent(queue, "RABBIT_EVENT");
	}

}
