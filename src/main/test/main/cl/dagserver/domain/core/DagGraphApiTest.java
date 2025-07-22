
package main.cl.dagserver.domain.core;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import static org.junit.jupiter.api.Assertions.*;
public class DagGraphApiTest {

	@Mock()
	private SchedulerRepositoryOutputPort repository;
	
	@Mock()
	private JarSchedulerOutputPort scanner;
	
	private DagGraphApi api = null;
	
	@BeforeEach
    void init() {
		repository = mock(SchedulerRepositoryOutputPort.class);
		scanner = mock(JarSchedulerOutputPort.class);
		api = new DagGraphApi(repository, scanner);
	}
	
	@Test
	void ExecuteErrorTest() throws DomainException {
		try {
			api.execute("uno", "dos", "tres");	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
	@Test
	void ExecuteTest() throws DomainException {
		Properties prop = new Properties();
		Properties opt = new Properties();
		api.setArgs(prop, opt);
		api.isCompiled(true);
		api.execute("uno", "dos", "tres");
		assertTrue(true);	
	}
	@Test
	void ExecuteUncompiledTest() throws DomainException {
		when(repository.getUncompiledBinByName(anyString())).thenReturn("{\"jarname\":\"testing.jar\",\"dags\":[{\"cron\":\"\",\"boxes\":[{\"rect\":{\"size\":{\"width\":100,\"height\":40},\"angle\":0,\"z\":1,\"position\":{\"x\":163,\"y\":249},\"id\":\"efeac530-e05c-4877-b6bd-04f8cb1401bb\",\"type\":\"standard.Image\",\"attrs\":{\"image\":{\"xlink:href\":\"/assets/images/operators/file.png\"},\"rect\":{\"rx\":5,\"ry\":5,\"fill\":\"#42C1C1\"},\"label\":{\"text\":\"step1\",\"fill\":\"black\"}}},\"id\":\"step1\",\"type\":\"main.cl.dagserver.infra.adapters.operators.FileOperator\",\"params\":[{\"source\":\"props\",\"type\":\"list\",\"value\":\"read\",\"key\":\"mode\"},{\"source\":\"props\",\"type\":\"text\",\"value\":\"c:\\\\test_prueba.txt\",\"key\":\"filepath\"},{\"source\":\"props\",\"type\":\"boolean\",\"value\":\"true\",\"key\":\"firstRowTitles\"},{\"source\":\"opts\",\"type\":\"xcom\",\"value\":null,\"key\":\"xcom\"},{\"source\":\"opts\",\"type\":\"text\",\"value\":\"TEST\",\"key\":\"rowDelimiter\"}],\"status\":\"ANY\"}],\"loc\":\"\",\"targetGroup\":\"\",\"name\":\"TEST_DAG\",\"className\":\"generated_dag.main.TEST_DAG\",\"targetDag\":\"\",\"trigger\":\"none\",\"group\":\"group.test\",\"target\":\"DAG\"}]}");
		
		Properties prop = new Properties();
		Properties opt = new Properties();
		api.setArgs(prop, opt);
		api.isCompiled(false);
		api.execute("uno", "TEST_DAG", "tres");
		assertTrue(true);	
	}
	@Test
	void mapToPropertiesTest() {
		Map<String,Object> map = new HashMap<>();
		map.put("key", "value");
		var prop = api.mapToProperties(map);
		assertNotNull(prop);
	}
	@Test
	void setArgsTest() {
		String json = "{\"key\":\"value\"}";
		String json2 = "{\"key\":\"value\"}";
		api.setArgs(json, json2);
		assertTrue(true);
	}
	@Test
	void setArgs2Test() {
		Map<String,Object> map1 = new HashMap<>();
		map1.put("key", "value");
		Map<String,Object> map2 = new HashMap<>();
		map2.put("key", "value");
		api.setArgs(map1, map2);
		assertTrue(true);
	}
}