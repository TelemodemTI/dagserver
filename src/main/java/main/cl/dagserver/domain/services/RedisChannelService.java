package main.cl.dagserver.domain.services;

import java.util.Iterator;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
@ImportResource("classpath:properties-config.xml")
public class RedisChannelService extends BaseServiceComponent implements RedisChannelUseCase {

		
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
	
	@Override
	public Properties getRedisChannelProperties() throws DomainException {
		var redisList = repository.getProperties(redisPropkey);
		Properties properties = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = redisList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterRedis = iterator.next();
			if(!propertyParameterRedis.getValue().equals("redis_consumer_listener")) {
				properties.put(propertyParameterRedis.getName(),propertyParameterRedis.getValue());	
			}
		}
		return properties;
	}

	@Override
	public void raiseEvent(String channel, String message) throws DomainException {
		this.trigggerEvent(channel, "REDIS_EVENT");
	}

	@Override
	public Properties getRedisListeners() throws DomainException {
		var propertyList = repository.getProperties(redisPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getValue().equals("redis_consumer_listener")) {
				props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());	
			}
		}
		return props;
	}

}
