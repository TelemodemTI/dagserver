package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables;

import java.util.ArrayList;
import java.util.List;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;


public class ColumnsTable extends AbstractTable implements ScannableTable {

	 private static final String TABLE_NAME = "TABLE_NAME";
	 private static final String COLUMNS = "COLUMNS";
	 private static final String SCHEMAS = "SCHEMAS";
	 private static final String TABLES = "TABLES";
	
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add(TABLE_NAME, SqlTypeName.VARCHAR);
		builder.add("COLUMN_NAME", SqlTypeName.VARCHAR);
		builder.add("TYPE_NAME", SqlTypeName.VARCHAR);
		builder.add("SIZE", SqlTypeName.INTEGER);
		builder.add("IS_NULLABLE", SqlTypeName.BOOLEAN);
		return builder.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		list.add(new Object[] {"CATALOG","TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {SCHEMAS,"TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {SCHEMAS,"TABLE_CATALOG",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {TABLES,"TABLE_SCHEM",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {TABLES,TABLE_NAME,SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {COLUMNS,TABLE_NAME,SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {COLUMNS,"COLUMN_NAME",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {COLUMNS,"TYPE_NAME",SqlTypeName.VARCHAR,255,false});
		list.add(new Object[] {COLUMNS,"SIZE",SqlTypeName.INTEGER,11,false});
		list.add(new Object[] {COLUMNS,"IS_NULLABLE",SqlTypeName.BOOLEAN,2,false});
		return Linq4j.asEnumerable(list);
	}

}
