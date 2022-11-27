package main.domain.services;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import main.application.ports.input.GetLogsUseCase;
import main.domain.entities.Log;
import main.domain.repositories.SchedulerRepository;


@Service
public class GetLogsService implements GetLogsUseCase,Function<List<String>,String> {

	
	@Autowired
	SchedulerRepository repository;
	
	@Override
	public String apply(List<String> t) {
		JSONArray arr = new JSONArray();
		var list = repository.getLogs(t.get(1));
		for (Iterator<Log> iterator = list.iterator(); iterator.hasNext();) {
			Log log = iterator.next();
			JSONObject item = new JSONObject();
			item.put("dagname",log.getDagname());
			item.put("execdt",log.getExecDt().toString());
			item.put("id", log.getId());
			//item.put("log", log.getValue());
			arr.put(item);
		}
		return arr.toString();
	}

}
