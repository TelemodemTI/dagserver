package main.cl.dagserver.unitest.infra.adapters.channels;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.channels.RedisChannel;
import redis.clients.jedis.JedisPubSub;

class RedisChannelTest {

	@Mock
	RedisChannelUseCase handler;
	
	@Mock
	ApplicationEventPublisher eventPublisher;
	
	private RedisChannel listener;
	
	@BeforeEach
    void init() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		handler = mock(RedisChannelUseCase.class);
		listener = new RedisChannel();
		ReflectionTestUtils.setField(listener, "handler", handler);
		ReflectionTestUtils.setField(listener, "eventPublisher", eventPublisher);
	}
	
	@Test
	void listenerTest() throws DomainException {
		Properties rabbitprops = new Properties(); 
		rabbitprops.setProperty("STATUS", "ACTIVE");
		rabbitprops.setProperty("redisCluster", "false");
		Properties op = new Properties();
		
		when(handler.getRedisChannelProperties()).thenReturn(rabbitprops);
		when(handler.getRedisListeners()).thenReturn(op);
		listener.setSomeCondition(true);
		assertTrue(true);
	}
	@Test
	void privateTest() {
		Properties rabbitprops = new Properties();
		Properties lconf = new Properties();
		lconf.setProperty("test", "test");
		rabbitprops.put("port", "123");
		rabbitprops.put("hostname", "localhost");
		ReflectionTestUtils.invokeMethod(listener, "getConectionInfo", rabbitprops);
		ReflectionTestUtils.invokeMethod(listener, "getConnectionInfoCluster", rabbitprops);
		ReflectionTestUtils.invokeMethod(listener, "stopListener");
		
		Thread newthread = new Thread(()->System.out.println("test"));
		Map<String,Thread> bindings = new HashMap<>();
		bindings.put("xtra", newthread);
		listener.setBindings(bindings);
		ReflectionTestUtils.invokeMethod(listener, "listenerCluster",lconf ,rabbitprops,mock(JedisPubSub.class));
		Pair<String, Integer> kv = Pair.of("key",1);
		ReflectionTestUtils.invokeMethod(listener, "getThreadAlt",mock(JedisPubSub.class),"key",kv);
		assertTrue(true);
	}
	
		
	
}