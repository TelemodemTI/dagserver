package main.domain.services;

import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.application.ports.input.RedisChannelUseCase;
import main.domain.core.BaseServiceComponent;
import main.domain.exceptions.DomainException;
import main.domain.model.PropertyParameterDTO;

@Service
@ImportResource("classpath:properties-config.xml")
public class RedisChannelService extends BaseServiceComponent implements RedisChannelUseCase {

	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(RedisChannelService.class);
	
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
	
	@Override
	public Properties getRedisChannelProperties() throws DomainException {
		var propertyList = repository.getProperties(redisPropkey);
		Properties props = new Properties();
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			props.put(propertyParameterDTO.getName(),propertyParameterDTO.getValue());
		}
		return props;
	}

	@Override
	public void raiseEvent(String channel, String message) {
		try {
			var propertyList = repository.getProperties(channel);
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
				scanner.init().execute(jarname, dagname,"REDIS_EVENT");	
			}	
		} catch (Exception e) {
			log.error(e);
		}
		
	}

}