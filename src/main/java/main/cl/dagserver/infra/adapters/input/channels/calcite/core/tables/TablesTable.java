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


public class TablesTable extends AbstractTable implements ScannableTable {
	
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add("TABLE_SCHEM", SqlTypeName.VARCHAR);
		builder.add("TABLE_NAME", SqlTypeName.VARCHAR);
		builder.add("TABLE_TYPE", SqlTypeName.VARCHAR);
		builder.add("VIEW_DEFINITION", SqlTypeName.VARCHAR);
		return builder.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		list.add(new Object[] {"SCHEMAS","CATALOG","TABLE",""});
		list.add(new Object[] {"SCHEMAS","SCHEMAS","TABLE",""});
		list.add(new Object[] {"SCHEMAS","TABLES","TABLE",""});
		list.add(new Object[] {"SCHEMAS","COLUMNS","TABLE",""});
		return Linq4j.asEnumerable(list);
	}


}
