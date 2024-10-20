package main.cl.dagserver.infra.adapters.input.graphql.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.ChannelDTO;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.SessionDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;

class QueryResolverMapperImplTest {
	
	private QueryResolverMapperImpl mapper = new QueryResolverMapperImpl();
	
	@Test
	void toAgentTest() {
		AgentDTO dto = new AgentDTO();
		var rv = mapper.toAgent(dto);
		assertNotNull(rv);
	}
	
	@Test
	void toPropertyTest() {
		PropertyDTO dto = new PropertyDTO();
		var rv = mapper.toProperty(dto);
		assertNotNull(rv);
	}
	
	@Test
	void toUncompiledTest() {
		UncompiledDTO dto = new UncompiledDTO();
		var rv = mapper.toUncompiled(dto);
		assertNotNull(rv);
	}
	@Test
	void toAccountTest() {
		UserDTO dto = new UserDTO();
		var rv = mapper.toAccount(dto);
		assertNotNull(rv);
	}
	@Test
	void toChannelPropsTest() {
		ChannelPropsDTO dto = new ChannelPropsDTO();
		var rv = mapper.toChannelProps(dto);
		assertNotNull(rv);
	}
	@Test
	void toChannelTest() {
		ChannelDTO dto = new ChannelDTO();
		ChannelPropsDTO pdto = new ChannelPropsDTO();
		List<ChannelPropsDTO> list = new ArrayList<>();
		list.add(pdto);
		dto.setProps(list);
		var rv = mapper.toChannel(dto);
		assertNotNull(rv);
	}
	@Test
	void toSessionTest() {
		SessionDTO dto = new SessionDTO();
		dto.setRefreshToken("refresh");
		dto.setToken("token");
		var rv = mapper.toSession(dto);
		assertNotNull(rv);
	}
}