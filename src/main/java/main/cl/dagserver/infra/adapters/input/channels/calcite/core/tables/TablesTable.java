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

public class TablesTable extends BaseTable implements ScannableTable {
	
	private static final String TABLE = "TABLE";
	private static final String SCHEMAS = "SCHEMAS";
	
	public TablesTable() {
		super();
	}
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder2 = typeFactory.builder();
		builder2.add("TABLE_SCHEM", SqlTypeName.VARCHAR);
		builder2.add("TABLE_NAME", SqlTypeName.VARCHAR);
		builder2.add("TABLE_TYPE", SqlTypeName.VARCHAR);
		return builder2.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		try {
			list = this.schemaForTable();
			list.add(new Object[] {SCHEMAS,"CATALOG",TABLE});
			list.add(new Object[] {SCHEMAS,SCHEMAS,TABLE});
			list.add(new Object[] {SCHEMAS,"TABLES",TABLE});
			list.add(new Object[] {SCHEMAS,"COLUMNS",TABLE});
			list.add(new Object[] {SCHEMAS,"PROCEDURES",TABLE});
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "JDBC CALCITE TABLE"));
		}
		return Linq4j.asEnumerable(list);
	}


}
