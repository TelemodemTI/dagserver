package main.cl.dagserver.infra.adapters.input.calcite.internal;

import org.apache.calcite.DataContext;
import org.apache.calcite.adapter.enumerable.EnumerableCollect;
import org.apache.calcite.config.CalciteConnectionConfig;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.EnumerableDefaults;
import org.apache.calcite.linq4j.Enumerator;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.Schema.TableType;
import org.apache.calcite.schema.Statistic;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.SqlCall;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyApiTable extends AbstractTable implements ScannableTable  {

	@Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
        // Define el esquema de la tabla con una sola columna
        return typeFactory.builder()
            .add("VALUE", SqlTypeName.VARCHAR, 255) // Columna de tipo VARCHAR
            .build();
    }


	@Override
    public Enumerable<Object[]> scan(DataContext root) {
        List<Object[]> list = new ArrayList<>();
        Object[] obarr = new Object[] {"test"};
		list.add(obarr);
    	return Linq4j.asEnumerable(list);
    }
}
