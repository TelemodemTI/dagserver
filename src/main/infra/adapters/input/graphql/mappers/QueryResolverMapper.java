package main.infra.adapters.input.graphql.mappers;

import org.mapstruct.Mapper;
import main.domain.model.AgentDTO;
import main.domain.model.ChannelDTO;
import main.domain.model.PropertyDTO;
import main.domain.model.UncompiledDTO;
import main.domain.model.UserDTO;
import main.infra.adapters.input.graphql.types.Account;
import main.infra.adapters.input.graphql.types.Agent;
import main.infra.adapters.input.graphql.types.Channel;
import main.infra.adapters.input.graphql.types.Property;
import main.infra.adapters.input.graphql.types.Uncompiled;

@Mapper(componentModel = "spring")
public interface QueryResolverMapper {
	
	public Agent toAgent(AgentDTO dto);	
	public Property toProperty(PropertyDTO dto);
	public Uncompiled toUncompiled(UncompiledDTO dto);
	public Account toAccount(UserDTO elt);
	public Channel toChannel(ChannelDTO dto);
}
