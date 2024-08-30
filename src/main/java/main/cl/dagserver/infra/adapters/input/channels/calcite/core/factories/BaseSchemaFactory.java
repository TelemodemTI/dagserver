package main.cl.dagserver.infra.adapters.input.channels.calcite.core.factories;

import java.util.Map;
import org.apache.calcite.schema.Schema;
import org.apache.calcite.schema.SchemaFactory;
import org.apache.calcite.schema.SchemaPlus;

import main.cl.dagserver.infra.adapters.input.channels.calcite.core.schemas.BaseApiSchema;

public class BaseSchemaFactory implements SchemaFactory {

	@Override
	public Schema create(SchemaPlus parentSchema, String name, Map<String, Object> operand) {
		return new BaseApiSchema();
	}

}
