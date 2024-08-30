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

public class CatalogTable extends AbstractTable implements ScannableTable {

	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add("TABLE_CAT", SqlTypeName.VARCHAR);
		return builder.build();
	}

	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		Object[] row = new Object[1];
		row[0] = "DEFAULT";
		list.add(row);
		return Linq4j.asEnumerable(list);
	}

}
