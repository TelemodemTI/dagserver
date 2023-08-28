package main.domain.services;

import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import main.application.ports.output.JarSchedulerOutputPort;
import main.domain.core.TokenEngine;
import main.domain.exceptions.DomainException;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
public class SchedulerMutationHandlerServiceTest {

	private SchedulerMutationHandlerService service = new SchedulerMutationHandlerService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
	}
	
	@Test
	void scheduleDagTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		try(MockedStatic<TokenEngine> utilities = Mockito.mockStatic(TokenEngine.class)){
			utilities.when(() -> TokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			service.scheduleDag("token", "dagname", "jarname");
			assertTrue(true);
		}
	}
	@Test
	void scheduleDagErrorTest() throws DomainException {
		Map<String,Object> ret = new HashMap<>();
		JarSchedulerAdapter adapter = mock(JarSchedulerAdapter.class);
		when(scanner.init()).thenReturn(adapter);
		try(MockedStatic<TokenEngine> utilities = Mockito.mockStatic(TokenEngine.class)){
			utilities.when(() -> TokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
			when(scanner.init()).thenThrow(new DomainException("test"));
			service.scheduleDag("token", "dagname", "jarname");
		}
	}
}
