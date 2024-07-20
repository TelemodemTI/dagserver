package main.cl.dagserver.infra.adapters.input.calcite.internal;
import org.apache.calcite.schema.Table;
import org.apache.calcite.schema.impl.AbstractSchema;

import java.util.HashMap;
import java.util.Map;

public class MyApiSchema extends AbstractSchema {
    @Override
    protected Map<String, Table> getTableMap() {
        Map<String, Table> map = new HashMap<>();
        map.put("MY_TABLE", new MyApiTable());
        return map;
    }
}
