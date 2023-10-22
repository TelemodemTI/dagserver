package main.cl.dagserver.infra.adapters.output.repositories.mappers;

import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.output.repositories.entities.EventListener;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Log;
import main.cl.dagserver.infra.adapters.output.repositories.entities.PropertyParameter;
import main.cl.dagserver.infra.adapters.output.repositories.entities.User;

public interface SchedulerMapper {

	public LogDTO toLogDTO(Log log);
	public EventListenerDTO toEventListenerDTO(EventListener eventListener);
	public UserDTO toUserDTO(User user);
	public PropertyParameterDTO toPropertyParameterDTO(PropertyParameter prop);
	
}
