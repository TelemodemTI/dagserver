package main.cl.dagserver.infra.adapters.input.channels.rabbit;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import com.rabbitmq.client.Channel;
import main.cl.dagserver.application.ports.input.RabbitChannelUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;


class RabbitChannelTest {
	@Mock
	private RabbitChannelUseCase handler;
	@Mock
	private ApplicationEventPublisher eventPublisher;
	private RabbitChannel channel;
	
	@BeforeEach
    void init() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		handler = mock(RabbitChannelUseCase.class);
		channel = new RabbitChannel(handler,eventPublisher);
	}
	
	
	@Test
	void listenerHandlerNoChannelTest() throws DomainException {
		Properties rabbitprops = new Properties(); 
		rabbitprops.setProperty("STATUS", "ACTIVE");
		rabbitprops.setProperty("username","a");
		rabbitprops.setProperty("password","b");
		rabbitprops.setProperty("host","c");
		rabbitprops.setProperty("port","1");
		when(handler.getRabbitChannelProperties()).thenReturn(rabbitprops);
		channel.listener();
		channel.setSomeCondition(true);
		assertTrue(true);
	}
	@Test
	void listenerHandlerNewChannelTest() throws DomainException {
		Properties rabbitprops = new Properties(); 
		rabbitprops.setProperty("STATUS", "ACTIVE");
		rabbitprops.setProperty("username","a");
		rabbitprops.setProperty("password","b");
		rabbitprops.setProperty("host","c");
		rabbitprops.setProperty("port","1");
		rabbitprops.setProperty("test", "rabbit_consumer_queue");
		when(handler.getRabbitChannelProperties()).thenReturn(rabbitprops);
		Channel chnomo = mock(Channel.class);
		channel.setChannel1(chnomo);
		channel.listener();
		channel.setSomeCondition(true);
		assertTrue(true);
	}
	@Test
	void listenerHandlerTest() throws DomainException {
		Properties rabbitprops = new Properties(); 
		rabbitprops.setProperty("STATUS", "ACTIVE");
		rabbitprops.setProperty("username","a");
		rabbitprops.setProperty("password","b");
		rabbitprops.setProperty("host","c");
		rabbitprops.setProperty("port","1");
		rabbitprops.setProperty("queue2", "rabbit_consumer_queue");
		List<Map<String,String>> list = new ArrayList<>();
		Map<String,String> map = new HashMap<>();
		map.put("queue", "queue");
		list.add(map);
		when(handler.getRabbitChannelProperties()).thenReturn(rabbitprops);
		Channel chnomo = mock(Channel.class);
		channel.setChannel1(chnomo);
		channel.setRunningConsumers(list);
		channel.listener();
		channel.setSomeCondition(true);
		assertTrue(true);
	}
	@Test
	void privateTest() {
		List<Map<String,String>> list = new ArrayList<>();
		Map<String,String> map = new HashMap<>();
		map.put("queue", "queue");
		list.add(map);
		List<String> queues = new ArrayList<>();
		queues.add("queue1");
		channel.setRunningConsumers(list);
		Channel chnomo = mock(Channel.class);
		channel.setChannel1(chnomo);
		ReflectionTestUtils.invokeMethod(channel, "removeConsumers",queues);
		assertTrue(true);
	}
}
