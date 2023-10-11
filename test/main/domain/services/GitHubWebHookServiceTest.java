package main.domain.services;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;
import main.domain.exceptions.DomainException;
import main.domain.model.PropertyParameterDTO;

public class GitHubWebHookServiceTest {

	private GitHubWebHookService service = new GitHubWebHookService();
	
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
		ReflectionTestUtils.setField(service, "gitHubPropkey", "gitHubPropkey");
		ReflectionTestUtils.setField(service, "jwtTtl", 1);
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
