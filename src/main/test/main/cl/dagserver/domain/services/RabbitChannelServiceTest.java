package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.core.TokenEngine;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;

public class RabbitChannelServiceTest {

	private RabbitChannelService service = new RabbitChannelService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@Mock 
	protected SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	@Mock
	protected TokenEngine tokenEngine;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		tokenEngine = mock(TokenEngine.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "tokenEngine", tokenEngine);
		ReflectionTestUtils.setField(service, "jwtSecret", "jwtSecret");
		ReflectionTestUtils.setField(service, "jwtSigner", "jwtSigner");
		ReflectionTestUtils.setField(service, "jwtSubject", "jwtSubject");
		ReflectionTestUtils.setField(service, "rabbitPropkey", "rabbitPropkey");
		ReflectionTestUtils.setField(service, "jwtTtl", 1);
	}
	
	@Test
	void getRabbitChannelPropertiesTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("value");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		var rt = service.getRabbitChannelProperties();
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
		service.raiseEvent("body", "queue", "routing", "type");
		assertTrue(true);
	}
}
