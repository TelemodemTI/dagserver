package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyInt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;

import main.cl.dagserver.application.ports.output.CompilerOutputPort;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.core.TokenEngine;
import main.cl.dagserver.domain.exceptions.DomainException;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;

class StageApiServiceTest {

	private StageApiService service = new StageApiService();
	
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
	}
	
	@Test
	void executeTmpTest() throws JSONException, DomainException {
		JSONArray boxes = new JSONArray();
		JSONObject dag = new JSONObject();
		dag.put("name", "name");
		dag.put("boxes", boxes);
		JSONObject rv = new JSONObject();
		JSONArray arr = new JSONArray();
		arr.put(dag);
		rv.put("dags", arr);
		when(repository.getUncompiledBin(anyInt())).thenReturn(rv.toString());
		var rv1 = service.executeTmp(1, "dagname", "stepname", "token");
		assertNotNull(rv1);
	}
}
