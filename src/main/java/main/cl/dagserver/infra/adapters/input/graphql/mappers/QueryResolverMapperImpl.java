package main.cl.dagserver.infra.adapters.input.graphql.mappers;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.ChannelDTO;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.types.Account;
import main.cl.dagserver.infra.adapters.input.graphql.types.Agent;
import main.cl.dagserver.infra.adapters.input.graphql.types.Channel;
import main.cl.dagserver.infra.adapters.input.graphql.types.ChannelProps;
import main.cl.dagserver.infra.adapters.input.graphql.types.Property;
import main.cl.dagserver.infra.adapters.input.graphql.types.Uncompiled;

@Component
public class QueryResolverMapperImpl implements QueryResolverMapper {

	@Override
	public Agent toAgent(AgentDTO dto) {
		Agent ag = new Agent();
		ag.setHostname(dto.getHostname());
		ag.setId(dto.getId());
		ag.setName(dto.getName());
		ag.setUpdatedOn(dto.getUpdatedOn());
		return ag;
	}

	@Override
	public Property toProperty(PropertyDTO dto) {
		Property p = new Property();
		p.setDescription(dto.getDescription());
		p.setGroup(dto.getGroup());
		p.setId(dto.getId());
		p.setName(dto.getName());
		p.setValue(dto.getValue());
		return p;
	}

	@Override
	public Uncompiled toUncompiled(UncompiledDTO dto) {
		Uncompiled u = new Uncompiled();
		u.setBin(dto.getBin());
		u.setCreatedDt(dto.getCreatedDt());
		u.setUncompiledId(dto.getUncompiledId());
		return u;
	}

	@Override
	public Account toAccount(UserDTO elt) {
		Account a = new Account();
		a.setId(elt.getId());
		a.setTypeAccount(elt.getTypeAccount());
		a.setUsername(elt.getUsername());
		return a;
	}

	@Override
	public Channel toChannel(ChannelDTO dto) {
		List<ChannelProps> props = new ArrayList<>();
		if(dto.getProps()!=null) {
			props = dto.getProps().stream().map(this::toChannelProps).toList();
		}
		Channel c = new Channel();
		c.setName(dto.getName());
		c.setProps(props);
		c.setStatus(dto.getStatus());
		c.setIcon(dto.getIcon());
		return c;
	}

	@Override
	public ChannelProps toChannelProps(ChannelPropsDTO dto) {
		ChannelProps cp = new ChannelProps();
		cp.setDescr(dto.getDescr());
		cp.setKey(dto.getKey());
		cp.setValue(dto.getValue());
		return cp;
	}

}
