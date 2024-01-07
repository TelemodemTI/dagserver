package main.cl.dagserver.domain.services;

import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.KafkaChannelUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
@ImportResource("classpath:properties-config.xml")
public class KafkaChannelService extends BaseServiceComponent implements KafkaChannelUseCase {

		
	@Value( "${param.kafka.propkey}" )
	private String kafkaPropkey;
	
	
	

	@Override
	public void raiseEvent(String topic, String message) throws DomainException {
		this.trigggerEvent(topic, "KAFKA_EVENT");
	}

	

	@Override
	public Properties getKafkaChannelProperties() throws DomainException {
		var propertyList = repository.getProperties(kafkaPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(!propertyParameterDTO.getValue().equals("kafka_consumer_listener")) {
				props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());	
			}
		}
		return props;
	}
	
	@Override
	public Properties getKafkaConsumers() throws DomainException {
		var propertyList = repository.getProperties(kafkaPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getValue().equals("kafka_consumer_listener")) {
				props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());	
			}
		}
		return props;
	}
}
