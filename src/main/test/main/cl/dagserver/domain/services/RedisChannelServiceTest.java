package main.cl.dagserver.domain.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

class RedisChannelServiceTest {

	private RedisChannelService service = new RedisChannelService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@Mock 
	protected SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	@Mock
	protected AuthenticationOutputPort tokenEngine;
	
	@BeforeEach
    void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		tokenEngine = mock(AuthenticationOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "auth", tokenEngine);
		ReflectionTestUtils.setField(service, "redisPropkey", "redisPropkey");
	}
	@Test
	void getRedisChannelPropertiesTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("redis_consumer_listener1");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		var rt = service.getRedisChannelProperties();
		assertNotNull(rt);
	}
	@Test
	void raiseEventTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("value");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		service.raiseEvent("channel","message");
		assertTrue(true);
	}
	@Test
	void getRedisListenersTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("redis_consumer_listener");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		var rt = service.getRedisListeners();
		assertNotNull(rt);
	}
}