package main.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.application.ports.input.LoginUseCase;
import main.application.ports.input.SchedulerQueryUseCase;
import main.infra.adapters.input.graphql.mappers.QueryResolverMapper;

public class QueryResolverTest {

	private QueryResolver resolver = new QueryResolver();
	
	@Mock
	SchedulerQueryUseCase handler;
	
	@Mock
	LoginUseCase login;
	
	@Mock
	QueryResolverMapper mapper;
	
	@BeforeEach
    public void init() {
		handler = mock(SchedulerQueryUseCase.class);
		login = mock(LoginUseCase.class);
		mapper = mock(QueryResolverMapper.class);
		ReflectionTestUtils.setField(resolver, "handler", handler);
		ReflectionTestUtils.setField(resolver, "login", login);
		ReflectionTestUtils.setField(resolver, "mapper", mapper);
	}
	@Test
	void loginTest() {
		when(login.apply(anyList())).thenReturn("test");
		var test = resolver.login("test", "test");
		assertEquals("test", test);
	}
}
