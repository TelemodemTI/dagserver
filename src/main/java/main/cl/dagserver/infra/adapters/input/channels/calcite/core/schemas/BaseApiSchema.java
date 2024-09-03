package main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas;

import java.util.HashMap;
import java.util.Map;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.CatalogTable;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.ColumnsTable;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.SchemasTable;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.TablesTable;
public class BaseApiSchema extends AbstractSchema {

	@Override
	protected Map<String, Table> getTableMap() {
		Map<String, Table> map = new HashMap<>();
		map.put("CATALOG", new CatalogTable());
		map.put("SCHEMAS", new SchemasTable());
		map.put("TABLES", new TablesTable());
		map.put("COLUMNS", new ColumnsTable());
		return map;
	}
	
}
