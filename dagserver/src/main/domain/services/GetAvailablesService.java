package main.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.application.ports.input.GetAvailablesUseCase;
import main.application.ports.output.JarSchedulerOutputPort;


@Service
public class GetAvailablesService implements GetAvailablesUseCase {

	@Autowired
	JarSchedulerOutputPort scanner;
	
	
	
	private List<Map<String, String>> getDefaults(){
		var rv = new ArrayList<Map<String, String>>();
		var bsd = new HashMap<String, String>();
		bsd.put("dagname","background_system_dag");
		bsd.put("groupname", "system_dags");
		bsd.put("cronExpr", "0 0/1 * * * ?");
		bsd.put("onStart", "");
		bsd.put("OnEnd", "");
		rv.add(bsd);
		
		var bsd1 = new HashMap<String, String>();
		bsd1.put("dagname","event_system_dag");
		bsd1.put("groupname", "system_dags");
		bsd1.put("cronExpr", "");
		bsd1.put("onStart", "");
		bsd1.put("OnEnd", "background_system_dag");
		
		rv.add(bsd1);
		
		return rv;
	}
	
	@Override
	public String apply(List<String> t) {
		try {
			JSONObject obj = new JSONObject();
			var rv = scanner.init().getOperators();
			rv.put("SYSTEM", getDefaults());
			var keys = rv.keySet();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				JSONArray arr = new JSONArray();
				List<Map<String, String>> iterable = rv.get(string);
				for (Iterator<Map<String, String>> iterator2 = iterable.iterator(); iterator2.hasNext();) {
					Map<String, String> dag = iterator2.next();
					JSONObject detail = new JSONObject();
					detail.put("dagname", dag.get("dagname"));
					detail.put("groupname", dag.get("groupname"));
					detail.put("cronExpr", dag.get("cronExpr"));
					detail.put("onStart", dag.get("onStart"));
					detail.put("onEnd", dag.get("onEnd"));
					arr.put(detail);
				}
				obj.put(string, arr);
			}
			return obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		
	}

}
