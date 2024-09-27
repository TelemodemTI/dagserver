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
import org.checkerframework.checker.nullness.qual.Nullable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

public class DagTable extends BaseTable implements ScannableTable {

	private String schema;
	private String tableName;
	public DagTable(String schema,String tableName) {
		super();
		this.schema = schema;
		this.tableName = tableName;
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
				eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "JDBC CALCITE DAGTABLE"));
			}
		}
		return builder.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		try {
			list = this.schemaForDag(schema, tableName);
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "JDBC CALCITE DAGTABLE"));
		}
		return Linq4j.asEnumerable(list);
	}

}
