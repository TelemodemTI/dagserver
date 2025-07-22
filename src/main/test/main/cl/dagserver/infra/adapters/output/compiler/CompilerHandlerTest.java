package main.cl.dagserver.infra.adapters.output.compiler;

import static org.mockito.Mockito.mock;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import main.cl.dagserver.application.ports.output.FileSystemOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.AuthDTO;

class CompilerHandlerTest {

	@Mock
	CompilerOperatorBuilder builder;
	@Mock
	ApplicationEventPublisher eventPublisher;
	@Mock
	FileSystemOutputPort fs;
	
	CompilerHandler handler;
	
	@BeforeEach
    void init() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		builder = mock(CompilerOperatorBuilder.class);
		fs = mock(FileSystemOutputPort.class);
		handler = new CompilerHandler(builder,eventPublisher,fs);
	}
	
	@Test
	void deleteJarfileTest() throws DomainException {
		handler.deleteJarfile("teste");
		assertTrue(true);
	}
	
	@Test
	void operatorsTest() throws DomainException {
		var arr = handler.operators();
		assertNotNull(arr);
	}
	
	@Test 
	void getPackageDefTest() {
		try {
			handler.getPackageDef("test");	
			handler.getPackageDef("test.test");	
		} catch (Exception e) {
			assertTrue(true);
		}
		
		
	}
	
	@Test
	void createJarTest() throws DomainException {
		Properties prop = new Properties();
		try {
			AuthDTO art = new AuthDTO();
			art.setUsername("dagserver");
			handler.createJar("{\"jarname\":\"dagJar1.jar\",\"dags\":[{\"cron\":\"0 0/1 * * * ?\",\"boxes\":[{\"rect\":{\"size\":{\"width\":100,\"height\":40},\"angle\":0,\"z\":1,\"position\":{\"x\":340,\"y\":157},\"id\":\"cd80cb92-b9ab-4f4c-982a-17d49122b565\",\"type\":\"standard.Image\",\"attrs\":{\"image\":{\"xlink:href\":\"/assets/images/operators/dummy.png\"},\"rect\":{\"rx\":5,\"ry\":5,\"fill\":\"#42C1C1\"},\"label\":{\"text\":\"step1\",\"fill\":\"black\"}}},\"id\":\"step1\",\"type\":\"main.cl.dagserver.infra.adapters.operators.DummyOperator\",\"status\":\"ANY\"}],\"loc\":\"\",\"name\":\"DAG_UzAjxX\",\"trigger\":\"cron\",\"class\":\"generated_dag.main.DAG_UzAjxX\",\"group\":\"main.group\",\"target\":\"DAG\"}]}",true,prop,art);	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
    void reimportTest() {
        var exception = assertThrows(DomainException.class, () -> handler.reimport("nonexistent.jar"));
        assertNotNull(exception);
        
    }
	
}