package main.cl.dagserver.infra.adapters.output.repositories;

import static org.testng.Assert.assertNotNull;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InternalStorageTest {

	private InternalStorage storage;
	
	@BeforeEach
    void init() {
		storage = new InternalStorage("c:\\tmp\\dagrags\\example_test");
		storage.init("2000000000");	
	}
	
	@Test
	void getLocatedbTest() throws JSONException {
		var str = storage.getLocatedb();
		assertNotNull(str);
		var json = new JSONObject();
		json.put("test", "test");
		storage.put(json);
		var json2 = storage.get();
		assertNotNull(json2);
	}
}
