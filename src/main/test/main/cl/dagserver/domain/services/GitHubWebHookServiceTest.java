package main.cl.dagserver.domain.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

class GitHubWebHookServiceTest {

	private GitHubWebHookService service = new GitHubWebHookService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@Mock 
	protected SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "gitHubPropkey", "gitHubPropkey");
	}
	@Test
	void getChannelPropsFromRepoTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("value");
		prop.setDescription("test");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		var rt = service.getChannelPropsFromRepo("test");
		assertNotNull(rt);
	}
	@Test
	void raiseEventTest() throws DomainException {
		PropertyParameterDTO prop = new PropertyParameterDTO();
		prop.setId(1);
		prop.setName("name");
		prop.setValue("value");
		prop.setDescription("test");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(prop);
		when(repository.getProperties(anyString())).thenReturn(props);
		service.raiseEvent("reporul");
		assertTrue(true);
	}
}
