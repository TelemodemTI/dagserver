package main.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import main.application.ports.input.SchedulerMutationUseCase;
import main.domain.exceptions.DomainException;
class MutationResolverTest {

	private MutationResolver mutation = new MutationResolver();
	
	@Mock
	SchedulerMutationUseCase handler;
	
	@BeforeEach
    void init() {
		handler = mock(SchedulerMutationUseCase.class);
		ReflectionTestUtils.setField(mutation, "handler", handler);
	}
	@Test
	void scheduleDagTest() throws DomainException {
		var resp = mutation.scheduleDag("test", "etst", "test");
		assertNotNull(resp);
	}
	@Test
	void scheduleDagErrorTest() throws DomainException {
		doThrow(new DomainException("test")).when(handler).scheduleDag(anyString(),anyString(),anyString());
		var resp = mutation.scheduleDag("test", "etst", "test");
		assertNotNull(resp);
	}
	@Test
	void unscheduleDagTest() throws DomainException {
		var resp = mutation.unscheduleDag(anyString(),anyString(), anyString());
		assertNotNull(resp);
	}
}
