package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables;

import java.util.ArrayList;
import java.util.List;
import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;

public class SchemasTable extends BaseTable implements ScannableTable {

	public SchemasTable() {
		super();
	}
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add("TABLE_SCHEM", SqlTypeName.VARCHAR);
		builder.add("TABLE_CAT", SqlTypeName.VARCHAR);
		return builder.build();	
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();		
		try {
			list = this.schemaForSchema();
			Object[] row = new Object[2];
			row[0] = "SCHEMAS";
			row[1] = "DEFAULT";
			list.add(row);
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "JDBC CALCITE SCHEMA"));
		}
		return Linq4j.asEnumerable(list);
	}

}
