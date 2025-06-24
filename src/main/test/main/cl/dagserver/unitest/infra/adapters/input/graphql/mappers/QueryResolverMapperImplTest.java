package main.cl.dagserver.unitest.infra.adapters.input.graphql.mappers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.SessionDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.mappers.QueryResolverMapperImpl;

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
	void toSessionTest() {
		SessionDTO dto = new SessionDTO();
		dto.setRefreshToken("refresh");
		dto.setToken("token");
		var rv = mapper.toSession(dto);
		assertNotNull(rv);
	}
}