package main.cl.dagserver.infra.adapters.output.compiler;

import static org.testng.Assert.assertNotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import main.cl.dagserver.domain.exceptions.DomainException;

class CompilerOperatorBuilderTest {
	
	private CompilerOperatorBuilder builder;
	
	@BeforeEach
    void init() {
		builder = new CompilerOperatorBuilder();
	}
	
	@Test
	void buildTest() throws DomainException, JSONException {
		JSONObject label = new JSONObject();
		label.put("text", "text");
		JSONObject attrs = new JSONObject();
		attrs.put("label", label);
		JSONObject source = new JSONObject();
		source.put("attrs", attrs);
		JSONObject box = new JSONObject();
		box.put("type", "main.cl.dagserver.infra.adapters.operators.DummyOperator");
		box.put("id", "id");
		box.put("status", "status");
		box.put("source", source);
		JSONArray boxes = new JSONArray();
		boxes.put(box);
		var impl = builder.build("jarname", boxes);
		assertNotNull(impl);
	}
}
