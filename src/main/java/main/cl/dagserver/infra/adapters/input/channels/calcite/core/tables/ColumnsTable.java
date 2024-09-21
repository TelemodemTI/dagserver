package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationContext;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.domain.services.SchedulerQueryHandlerService;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.mapper.DagTypeMapper;

@Log4j2
public class ColumnsTable extends AbstractTable implements ScannableTable {

	 private static final String TABLE_NAME = "TABLE_NAME";
	 private static final String COLUMNS = "COLUMNS";
	 private static final String SCHEMAS = "SCHEMAS";
	 private static final String TABLES = "TABLES";
	
	private SchedulerQueryHandlerService provider;
	private CalciteUseCase calcite;
	private DagTypeMapper mapper = new DagTypeMapper();
	 
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add(TABLE_NAME, SqlTypeName.VARCHAR);
		builder.add("COLUMN_TYPE", SqlTypeName.VARCHAR);
		builder.add("COLUMN_NAME", SqlTypeName.VARCHAR);
		builder.add("TYPE_NAME", SqlTypeName.VARCHAR);
		builder.add("SIZE", SqlTypeName.INTEGER);
		builder.add("IS_NULLABLE", SqlTypeName.BOOLEAN);
		return builder.build();
	}

	@Override
	@SuppressWarnings("static-access")
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		
		List<Object[]> list = new ArrayList<>();
		if(this.provider == null) {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			this.provider =  appCtx.getBean("schedulerQueryHandlerService", SchedulerQueryHandlerService.class);
			this.calcite =  appCtx.getBean("calciteService", CalciteUseCase.class);
		}
		try {
			var availables = this.provider.availableJobs();
			for (Map.Entry<String, List<Map<String, String>>> entry : availables.entrySet()) {
				for (Map<String, String> operatormap : entry.getValue()) {
					String rdagname = operatormap.get("dagname");
					List<String> arr = this.calcite.getSchemas(rdagname);
					for (Iterator<String> iterator = arr.iterator(); iterator.hasNext();) {
						String schema1 = iterator.next();
						List<String> tables = this.calcite.getTables(schema1);
						for (Iterator<String> iterator2 = tables.iterator(); iterator2.hasNext();) {
							String table = iterator2.next();
							List<Map<String,Object>> columns = this.calcite.getColumns(schema1,table);
							for (Iterator<Map<String,Object>> iterator3 = columns.iterator(); iterator3.hasNext();) {
								Map<String, Object> objects = iterator3.next();
								String typep = objects.get("type").toString().replace("class ", "");
								list.add(new Object[] {table,"TABLE",objects.get("name"),this.mapper.evaluate(typep),255,false});
							}
						}
					}
				}
			}
			list.add(new Object[] {"CATALOG","TABLE","TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {SCHEMAS,"TABLE","TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {SCHEMAS,"TABLE","TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {TABLES,"TABLE","TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {TABLES,"TABLE",TABLE_NAME,SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,"TABLE",TABLE_NAME,SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,"TABLE","COLUMN_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,"TABLE","COLUMN_TYPE",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,"TABLE","TYPE_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,"TABLE","SIZE",SqlTypeName.INTEGER,11,false});
			list.add(new Object[] {COLUMNS,"TABLE","IS_NULLABLE",SqlTypeName.BOOLEAN,2,false});
			list.add(new Object[] {"PROCEDURES","TABLE","PROCEDURE_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {"PROCEDURES","TABLE","PROCEDURE_TYPE",SqlTypeName.VARCHAR,255,false});
			
			list.add(new Object[] {"DAG_EXECUTOR","PROCEDURE","JARNAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {"DAG_EXECUTOR","PROCEDURE","DAGNAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {"DAG_EXECUTOR","PROCEDURE","ARGS",SqlTypeName.VARCHAR,255,false});
			
		} catch (Exception e) {
			log.error(e);
		}
		return Linq4j.asEnumerable(list);
	}

}
