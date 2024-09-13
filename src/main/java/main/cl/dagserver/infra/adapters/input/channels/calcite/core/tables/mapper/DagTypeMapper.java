package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.mapper;

import java.util.HashMap;
import java.util.Map;
import org.apache.calcite.sql.type.SqlTypeName;

import main.cl.dagserver.domain.exceptions.DomainException;

public class DagTypeMapper {
	private Map<String,SqlTypeName> mapper;
	
	public DagTypeMapper() {
		this.mapper = new HashMap<String,SqlTypeName>();
		this.mapper.put("java.lang.String", SqlTypeName.VARCHAR);
		this.mapper.put("java.lang.Integer", SqlTypeName.INTEGER);
		this.mapper.put("java.lang.Boolean", SqlTypeName.BOOLEAN);
		this.mapper.put("java.lang.Double", SqlTypeName.DOUBLE);
		this.mapper.put("java.lang.Long", SqlTypeName.BIGINT);
	}
	public SqlTypeName evaluate(String typep) throws DomainException {
		if(this.mapper.containsKey(typep)) {
			return this.mapper.get(typep);	
		} else {
			throw new DomainException(new Exception("unsupported data type"));
		}
		
	}
}
