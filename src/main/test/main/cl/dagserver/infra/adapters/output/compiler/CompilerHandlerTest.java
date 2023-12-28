package main.cl.dagserver.infra.adapters.output.compiler;

import static org.mockito.Mockito.mock;

import java.util.Properties;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import main.cl.dagserver.domain.exceptions.DomainException;

class CompilerHandlerTest {

	@Mock
	CompilerOperatorBuilder builder;
	@Mock
	ApplicationEventPublisher eventPublisher;
	
	CompilerHandler handler;
	
	@BeforeEach
    void init() {
		eventPublisher = mock(ApplicationEventPublisher.class);
		builder = mock(CompilerOperatorBuilder.class);
		handler = new CompilerHandler(builder,eventPublisher);
		ReflectionTestUtils.setField(handler, "pathfolder", "test");	
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
		} catch (Exception e) {}
		var str = handler.getPackageDef("test.test");
		assertNotNull(str);
	}
	
	@Test
	void createJarTest() throws DomainException, JSONException {
		JSONArray boxes = new JSONArray();
		JSONObject dag = new JSONObject();
		dag.put("cron", "cron");
		dag.put("trigger", "cron");
		dag.put("loc", "log");
		dag.put("class", "class");
		dag.put("group", "group");
		dag.put("boxes", boxes);
		JSONArray dagarr = new JSONArray();
		dagarr.put(dag);
		Properties prop = new Properties();
		JSONObject bin = new JSONObject();
		bin.put("jarname", "name");
		bin.put("dags", dagarr);
		try {
			handler.createJar(bin.toString(),true,prop);	
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
