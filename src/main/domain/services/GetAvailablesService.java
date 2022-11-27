package main.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.application.ports.input.GetAvailablesUseCase;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.QuartzOutputPort;
import main.domain.entities.EventListener;
import main.domain.repositories.SchedulerRepository;


@Service
public class GetAvailablesService implements GetAvailablesUseCase,Function<List<String>,String> {

	@Autowired
	JarSchedulerOutputPort scanner;
	
	@Autowired
	QuartzOutputPort quartz;
	
	@Autowired
	SchedulerRepository repository;
	
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
			List<Map<String,Object>> scheduled = quartz.listScheduled();
			for (Iterator<String> iterator = keys.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				JSONArray arr = new JSONArray();
				List<Map<String, String>> iterable = rv.get(string);
				for (Iterator<Map<String, String>> iterator2 = iterable.iterator(); iterator2.hasNext();) {
					Map<String, String> dag = iterator2.next();
					
					boolean isScheduled = false;
					for (Iterator<Map<String,Object>> iterator3 = scheduled.iterator(); iterator3.hasNext();) {
						Map<String,Object> map = iterator3.next();
						if(map.get("jobname").equals(dag.get("dagname"))) {
							isScheduled = true;
							break;
						}
					}
					
					if(!isScheduled) {
						var list = repository.listEventListeners();
						for (Iterator<EventListener> iterator4 = list.iterator(); iterator4.hasNext();) {
							EventListener eventListener = iterator4.next();
							if(eventListener.getListenerName().equals(dag.get("dagname"))) {
								isScheduled = true;
								break;
							}
						}	
					}
					
					JSONObject detail = new JSONObject();
					detail.put("dagname", dag.get("dagname"));
					detail.put("groupname", dag.get("groupname"));
					detail.put("cronExpr", dag.get("cronExpr"));
					detail.put("onStart", dag.get("onStart"));
					detail.put("onEnd", dag.get("onEnd"));
					detail.put("isScheduled", isScheduled);
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
