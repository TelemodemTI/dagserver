package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyInt;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;

import main.cl.dagserver.application.ports.output.AuthenticationOutputPort;
import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StageApiServiceTest {

	private StageApiService service = new StageApiService();
	
	@Mock
	protected JarSchedulerOutputPort scanner;
	
	@Mock 
	protected SchedulerRepositoryOutputPort repository;
	
	@Mock
	protected CompilerOutputPort compiler;
	
	@Mock
	protected AuthenticationOutputPort tokenEngine;
	
	@BeforeEach
    public void init() {
		scanner = mock(JarSchedulerOutputPort.class);
		repository = mock(SchedulerRepositoryOutputPort.class);
		compiler = mock(CompilerOutputPort.class);
		tokenEngine = mock(AuthenticationOutputPort.class);
		ReflectionTestUtils.setField(service, "scanner", scanner);
		ReflectionTestUtils.setField(service, "repository", repository);
		ReflectionTestUtils.setField(service, "compiler", compiler);
		ReflectionTestUtils.setField(service, "auth", tokenEngine);
		
	}
	
	@Test
	void executeTmpTest() throws DomainException {
		when(repository.getUncompiledBin(anyInt())).thenReturn("{\"jarname\":\"dagJar1.jar\",\"dags\":[{\"cron\":\"0 0/1 * * * ?\",\"boxes\":[{\"rect\":{\"size\":{\"width\":100,\"height\":40},\"angle\":0,\"z\":1,\"position\":{\"x\":101,\"y\":70},\"id\":\"05078a67-e245-41de-afbc-204c285c5cdb\",\"type\":\"standard.Image\",\"attrs\":{\"image\":{\"xlink:href\":\"/assets/images/operators/dummy.png\"},\"rect\":{\"rx\":5,\"ry\":5,\"fill\":\"#42C1C1\"},\"label\":{\"text\":\"step1\",\"fill\":\"black\"}}},\"id\":\"step1\",\"type\":\"main.cl.dagserver.infra.adapters.operators.DummyOperator\",\"status\":\"ANY\"}],\"loc\":\"\",\"name\":\"DAG_kvSFRQ\",\"trigger\":\"cron\",\"class\":\"generated_dag.main.DAG_kvSFRQ\",\"group\":\"main.group\",\"target\":\"DAG\"}]}");
		var rv1 = service.executeTmp(1, "DAG_kvSFRQ", "step1", "token");
		assertNotNull(rv1);
	}
	
}
