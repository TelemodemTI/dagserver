package main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas;

import java.util.HashMap;
import java.util.Map;
import com.google.common.collect.ArrayListMultimap;
import org.apache.calcite.schema.Function;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.apache.calcite.schema.impl.ScalarFunctionImpl;

import com.google.common.collect.Multimap;

import main.cl.dagserver.infra.adapters.input.channels.calcite.core.storedprocedure.DagStoredProcedure;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.CatalogTable;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.ColumnsTable;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.ProcedureTables;
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
		map.put("PROCEDURES", new ProcedureTables());
		return map;
	}
	 
	
	@Override
    protected Multimap<String, Function> getFunctionMultimap() {
        Multimap<String, Function> functionMap = ArrayListMultimap.create();
        functionMap.put("DAG_EXECUTOR", ScalarFunctionImpl.create(DagStoredProcedure.class, "run"));
        return functionMap;
    }
}
