package main.cl.dagserver.infra.adapters.input.calcite.internal;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import java.util.ArrayList;
import java.util.List;

public class MyApiTable extends AbstractTable implements ScannableTable  {

	@Override
    public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataType type = typeFactory.createSqlType(SqlTypeName.VARCHAR,255);    
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add("VALUE", type);
		return builder.build();
    }


	@Override
    public Enumerable<Object[]> scan(DataContext root) {
        List<Object[]> list = new ArrayList<>();
        Object[] obarr = new Object[] {"test"};
		list.add(obarr);
    	return Linq4j.asEnumerable(list);
    }
}
