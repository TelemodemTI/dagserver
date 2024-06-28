package main.cl.dagserver.infra.adapters.output.repositories;

import static org.testng.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nhl.dflib.DataFrame;

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
		Map<String,DataFrame> map = new HashMap<>();
		var df = DataFrame
        	.byArrayRow("status") 
        	.appender() 
        	.append("test")   
        	.toDataFrame();
		map.put("test", df);
		storage.put(map);
		var json2 = storage.get();
		assertNotNull(json2);
	}
}
