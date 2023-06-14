package main.infra.adapters.output.repositories.mappers;

import org.springframework.stereotype.Component;

import main.domain.model.EventListenerDTO;
import main.domain.model.LogDTO;
import main.domain.model.PropertyParameterDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.output.repositories.entities.EventListener;
import main.infra.adapters.output.repositories.entities.Log;
import main.infra.adapters.output.repositories.entities.PropertyParameter;
import main.infra.adapters.output.repositories.entities.User;

@Component
public class SchedulerMapper {
	public LogDTO toLogDTO(Log log){
		LogDTO dto = new LogDTO();
		dto.setDagname(log.getDagname());
		dto.setExecDt(log.getExecDt());
		dto.setId(log.getId());
		dto.setOutputxcom(log.getOutputxcom());
		dto.setStatus(log.getStatus());
		dto.setValue(log.getValue());
		return dto;
	}
	
	public EventListenerDTO toEventListenerDTO(EventListener eventListener) {
		EventListenerDTO dto = new EventListenerDTO();
		dto.setGroupName(eventListener.getGroupName());
		dto.setListenerName(eventListener.getListenerName());
		dto.setOnEnd(eventListener.getOnEnd());
		dto.setOnStart(eventListener.getOnStart());
		return dto;
	}
	
	public UserDTO toUserDTO(User user) {
		UserDTO dto = new UserDTO();
		dto.setCreatedAt(user.getCreatedAt());
		dto.setId(user.getId());
		dto.setPwdhash(user.getPwdhash());
		dto.setTypeAccount(user.getTypeAccount());
		dto.setUsername(user.getUsername());
		return dto;
	}
	
	public PropertyParameterDTO toPropertyParameterDTO(PropertyParameter prop) {
		PropertyParameterDTO dto = new PropertyParameterDTO();
		dto.setDescription(prop.getDescription());
		dto.setGroup(prop.getGroup());
		dto.setId(prop.getId());
		dto.setName(prop.getName());
		dto.setValue(prop.getValue());
		return dto;
	}
}
