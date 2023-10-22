package main.cl.dagserver.infra.adapters.output.repositories.mappers;

import org.springframework.stereotype.Component;

import main.cl.dagserver.domain.model.EventListenerDTO;
import main.cl.dagserver.domain.model.LogDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.output.repositories.entities.EventListener;
import main.cl.dagserver.infra.adapters.output.repositories.entities.Log;
import main.cl.dagserver.infra.adapters.output.repositories.entities.PropertyParameter;
import main.cl.dagserver.infra.adapters.output.repositories.entities.User;

@Component
public class SchedulerMapperImpl implements SchedulerMapper {

	@Override
	public LogDTO toLogDTO(Log log) {
		LogDTO l = new LogDTO();
		l.setChannel(log.getChannel());
		l.setDagname(log.getDagname());
		l.setExecDt(log.getExecDt());
		l.setId(log.getId());
		l.setMarks(log.getMarks());
		l.setOutputxcom(log.getOutputxcom());
		l.setStatus(log.getStatus());
		l.setValue(log.getValue());
		return l;
	}

	@Override
	public EventListenerDTO toEventListenerDTO(EventListener eventListener) {
		EventListenerDTO e = new EventListenerDTO();
		e.setGroupName(eventListener.getGroupName());
		e.setListenerName(eventListener.getListenerName());
		e.setOnEnd(eventListener.getOnEnd());
		e.setOnStart(eventListener.getOnStart());
		return e;
	}

	@Override
	public UserDTO toUserDTO(User user) {
		UserDTO u = new UserDTO();
		u.setCreatedAt(user.getCreatedAt());
		u.setId(user.getId());
		u.setPwdhash(user.getPwdhash());
		u.setTypeAccount(user.getTypeAccount());
		u.setUsername(user.getUsername());
		return u;
	}

	@Override
	public PropertyParameterDTO toPropertyParameterDTO(PropertyParameter prop) {
		PropertyParameterDTO p = new PropertyParameterDTO();
		p.setDescription(prop.getDescription());
		p.setGroup(prop.getGroup());
		p.setId(prop.getId());
		p.setName(prop.getName());
		p.setValue(prop.getValue());
		return p;
	}

}
