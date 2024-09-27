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

public class ColumnsTable extends BaseTable implements ScannableTable {
	private static final String TABLE = "TABLE";
	private static final String PROCEDURE = "PROCEDURE";
	private static final String DAG_EXECUTOR = "DAG_EXECUTOR";
	private static final String TABLE_NAME = "TABLE_NAME";
	private static final String COLUMNS = "COLUMNS";
	private static final String SCHEMAS = "SCHEMAS";
	private static final String TABLES = "TABLES";
	
	public ColumnsTable() {
		super();
	}
	
	 
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
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		
		List<Object[]> list = new ArrayList<>();
		
		try {
			list = this.schemaForColumn();
			list.add(new Object[] {"CATALOG",TABLE,"TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {SCHEMAS,TABLE,"TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {SCHEMAS,TABLE,"TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {TABLES,TABLE,"TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {TABLES,TABLE,TABLE_NAME,SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,TABLE,TABLE_NAME,SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,TABLE,"COLUMN_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,TABLE,"COLUMN_TYPE",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,TABLE,"TYPE_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {COLUMNS,TABLE,"SIZE",SqlTypeName.INTEGER,11,false});
			list.add(new Object[] {COLUMNS,TABLE,"IS_NULLABLE",SqlTypeName.BOOLEAN,2,false});
			list.add(new Object[] {"PROCEDURES",TABLE,"PROCEDURE_NAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {"PROCEDURES",TABLE,"PROCEDURE_TYPE",SqlTypeName.VARCHAR,255,false});
			
			list.add(new Object[] {DAG_EXECUTOR,PROCEDURE,"JARNAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {DAG_EXECUTOR,PROCEDURE,"DAGNAME",SqlTypeName.VARCHAR,255,false});
			list.add(new Object[] {DAG_EXECUTOR,PROCEDURE,"ARGS",SqlTypeName.VARCHAR,255,false});
			
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "JDBC CALCITE COLUMNS"));
		}
		return Linq4j.asEnumerable(list);
	}

}
