package main.infra.adapters.input.graphql.mappers;

import org.springframework.stereotype.Component;

import main.domain.model.AgentDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.input.graphql.types.Account;
import main.infra.adapters.input.graphql.types.Agent;
import main.infra.adapters.input.graphql.types.Property;
import main.infra.adapters.input.graphql.types.Uncompiled;

@Component
public class QueryResolverMapper {

	public Agent toAgent(AgentDTO dto) {
		Agent agent = new Agent();
		agent.setHostname(dto.getHostname());
		agent.setId(dto.getId());
		agent.setName(dto.getName());
		agent.setUpdatedOn(dto.getUpdatedOn());
		return agent;
	}
	
	public Property toProperty(PropertyDTO dto) {
		Property prop = new Property();
		prop.setDescription(dto.getDescription());
		prop.setGroup(dto.getGroup());
		prop.setName(dto.getName());
		prop.setValue(dto.getValue());
		return prop;
	}
	
	public Uncompiled toUncompiled(UncompiledDTO dto) {
		Uncompiled un = new Uncompiled();
		un.setBin(dto.getBin());
		un.setCreatedDt(dto.getCreatedDt());
		un.setUncompiledId(dto.getUncompiledId());
		return un;
	}

	public Account toAccount(UserDTO elt) {
		Account acc = new Account();
		acc.setId(elt.getId());
		acc.setTypeAccount(elt.getTypeAccount());
		acc.setUsername(elt.getUsername());
		return acc;
	}

	
	
}
