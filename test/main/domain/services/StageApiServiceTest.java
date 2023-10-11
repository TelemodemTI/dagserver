package main.domain.services;

import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.anyInt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.Test;
import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertNotNull;
import java.util.HashMap;
import java.util.Map;
import static org.mockito.ArgumentMatchers.anyString;

public class StageApiServiceTest {

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
	void executeTmpTest() {
		Map<String,String> claimsmap = new HashMap<>();
		claimsmap.put("typeAccount", "ADMIN");
		Map<String,Object> ret = new HashMap<>();
		ret.put("claims", claimsmap);
		when(tokenEngine.untokenize(anyString(),anyString(),anyString())).thenReturn(ret);
		JSONObject daguncompiled = new JSONObject();
		JSONObject item = new JSONObject();
		JSONArray boxes = new JSONArray();
		JSONObject box = new JSONObject();
		box.put("type", "main.infra.adapters.operators.DummyOperator");
		box.put("id", "test");
		box.put("status", "ANY");
		JSONObject source = new JSONObject();
		JSONObject attrs = new JSONObject();
		JSONObject labels = new JSONObject();
		labels.put("text", "name");
		attrs.put("label", labels);
		source.put("attrs", attrs);
		box.put("source", source);
		boxes.put(box);
		item.put("boxes", boxes);
		JSONArray arr = new JSONArray();
		arr.put(item);
		daguncompiled.put("dags", arr);
		when(repository.getUncompiledBin(anyInt())).thenReturn(daguncompiled.toString());
		var rt = service.executeTmp(1, "dagname", "stepname", "token");
		assertNotNull(rt);
	}
}
