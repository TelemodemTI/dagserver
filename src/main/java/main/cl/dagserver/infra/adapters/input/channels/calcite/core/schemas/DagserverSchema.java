package main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;
import org.springframework.context.ApplicationContext;

import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.DagTable;

public class DagserverSchema extends AbstractSchema  {

	private String schema = "";
	private CalciteUseCase calcite;
	
	public DagserverSchema(String schema) {
		this.schema = schema;
	}
	@SuppressWarnings("static-access")
	@Override
    protected Map<String, Table> getTableMap() {
		if(this.calcite == null) {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			this.calcite =  appCtx.getBean("calciteService", CalciteUseCase.class);	
		}
		List<String> tables = this.calcite.getTables(this.schema);
		Map<String, Table> tableMap = new HashMap<>();
		for (Iterator<String> iterator = tables.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			tableMap.put(string, new DagTable(this.schema,string));	
		}
        return tableMap;
    }
}
