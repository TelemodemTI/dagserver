package main.cl.dagserver.domain.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.model.LogDTO;
@Service
public class CalciteService extends BaseServiceComponent  implements CalciteUseCase {

	@Override
	public List<String> getSchemas(String dagname) {
		var logs = this.repository.getLogs(dagname);
		List<String> arr = new ArrayList<>();
		for (Iterator<LogDTO> iterator = logs.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			String value = logDTO.getOutputxcom();
			if(!arr.contains(value)) {
				arr.add(value);
			}
		}
		return arr;
	}

	@Override
	public List<String> getTables(String schema) {
		var map = this.storage.getEntry(schema);
		return new ArrayList<String>(map.keySet());
	}

	@Override
	public List<Map<String, Object>> getColumns(String schema, String table) {
		var map = this.storage.getEntry(schema);
	    var df = map.get(table);
	    Index columnIndex = df.getColumnsIndex();

	    List<Map<String, Object>> columns = new ArrayList<>();
	    
	    for (int i = 0; i < columnIndex.size(); i++) {
	        String columnName = columnIndex.getLabel(i);
	        Series<?> columnSeries = df.getColumn(i);
	        Map<String, Object> columnInfo = new HashMap<>();
	        columnInfo.put("name", columnName);    
	        columnInfo.put("type", columnSeries.getInferredType().toString());
	        
	        columns.add(columnInfo);
	    }
	    return columns;
	}

	@Override
	public List<String> getAllSchemas() {
		var logs = this.repository.getAllLogs();
		List<String> arr = new ArrayList<>();
		for (Iterator<LogDTO> iterator = logs.iterator(); iterator.hasNext();) {
			LogDTO logDTO = iterator.next();
			String value = logDTO.getOutputxcom();
			if(!arr.contains(value)) {
				arr.add(value);
			}
		}
		return arr;
	}

	@Override
	public Object getCell(String schema, String tableName, String columnName, Integer index) {
		var df = this.storage.getEntry(schema.substring(3)).get(tableName);
		var serie = df.getColumn(columnName);
		Object value = serie.get(index);
		return value.toString();
	}

	@Override
	public Integer getCount(String schema, String tableName) {
		var df = this.storage.getEntry(schema.substring(3)).get(tableName);
		return df.height();
	}
	
}
