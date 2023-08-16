package main.infra.adapters.input.graphql;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import main.application.ports.input.SchedulerMutationUseCase;
import main.domain.exceptions.DomainException;
public class MutationResolverTest {

	private MutationResolver mutation = new MutationResolver();
	
	@Mock
	SchedulerMutationUseCase handler;
	
	@BeforeEach
    public void init() {
		handler = mock(SchedulerMutationUseCase.class);
		ReflectionTestUtils.setField(mutation, "handler", handler);
	}
	@Test
	public void scheduleDagTest() throws DomainException {
		var resp = mutation.scheduleDag("test", "etst", "test");
		assertNotNull(resp);
	}
}
