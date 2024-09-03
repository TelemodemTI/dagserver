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
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationContext;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.DagTypeMapper;

@Log4j2
public class DagTable extends AbstractTable implements ScannableTable {

	private String schema;
	private String tableName;
	private CalciteUseCase calcite;
	private DagTypeMapper mapper = new DagTypeMapper();
	@SuppressWarnings("static-access")
	public DagTable(String schema,String tableName) {
		this.schema = schema;
		this.tableName = tableName;
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		this.calcite =  appCtx.getBean("calciteService", CalciteUseCase.class);	
	}

	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		List<Map<String,Object>> columns = this.calcite.getColumns(schema,tableName);
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		for (Iterator<Map<String, Object>> iterator = columns.iterator(); iterator.hasNext();) {
			try {
				Map<String, Object> map = iterator.next();
				String typep = map.get("type").toString().replace("class ", "");
				builder.add(map.get("name").toString(), this.mapper.evaluate(typep));	
			} catch (Exception e) {
				log.error(e);
			}
		}
		return builder.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Map<String,Object>> columns = this.calcite.getColumns(schema,tableName);
		List<Object[]> list = new ArrayList<>();
		Integer count = this.calcite.getCount("SCH"+schema,tableName);		
		for (int i = 0; i < count; i++) {
			var index = 0;
			var obj = new Object[columns.size()];
			for (Iterator<Map<String, Object>> iterator = columns.iterator(); iterator.hasNext();) {
				Map<String, Object> map = iterator.next();
				String columnName = map.get("name").toString();
				obj[index] = this.calcite.getCell("SCH"+schema,tableName,columnName,i);
				index++;	
			}
			list.add(obj);
			
		}
		
		
		
		return Linq4j.asEnumerable(list);
	}

}
