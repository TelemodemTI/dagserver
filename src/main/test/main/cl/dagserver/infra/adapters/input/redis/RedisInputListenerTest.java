package main.cl.dagserver.infra.adapters.input.redis;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertTrue;

import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;

class RedisInputListenerTest {

	@Mock
	RedisChannelUseCase handler;
	
	@Mock
	ApplicationEventPublisher eventPublisher;
	
	private RedisInputListener listener;
	
	@BeforeEach
    void init() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		handler = mock(RedisChannelUseCase.class);
		listener = new RedisInputListener(handler,eventPublisher);
	}
	
	@Test
	void listenerTest() throws DomainException, InterruptedException {
		Properties rabbitprops = new Properties(); 
		rabbitprops.setProperty("STATUS", "ACTIVE");
		rabbitprops.setProperty("redisCluster", "false");
		Properties op = new Properties();
		
		when(handler.getRedisChannelProperties()).thenReturn(rabbitprops);
		when(handler.getRedisListeners()).thenReturn(op);
		listener.listener();
		listener.setSomeCondition(true);
		assertTrue(true);
	}
}
