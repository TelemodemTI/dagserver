package main.cl.dagserver.application.ports.input;

import java.util.List;
import java.util.Map;

public interface CalciteUseCase {
	List<String> getSchemas(String dagname);
	List<String> getTables(String schema);
	List<Map<String, Object>> getColumns(String schema, String table);
	List<String> getAllSchemas();
	Object getCell(String schema, String tableName, String columnName, Integer index);
	Integer getCount(String schema, String tableName);
}
