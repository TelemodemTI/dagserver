package main.infra.adapters.output.repositories.mappers;

import org.mapstruct.Mapper;

import main.domain.model.EventListenerDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyParameterDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.output.repositories.entities.EventListener;
import main.infra.adapters.output.repositories.entities.Log;
import main.infra.adapters.output.repositories.entities.PropertyParameter;
import main.infra.adapters.output.repositories.entities.User;

@Mapper(componentModel = "spring")
public interface SchedulerMapper {

	public LogDTO toLogDTO(Log log);
	public EventListenerDTO toEventListenerDTO(EventListener eventListener);
	public UserDTO toUserDTO(User user);
	public PropertyParameterDTO toPropertyParameterDTO(PropertyParameter prop);
	
}
