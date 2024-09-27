package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.calcite.schema.impl.AbstractTable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import main.cl.dagserver.application.ports.input.CalciteUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.services.SchedulerQueryHandlerService;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;
import main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables.mapper.DagTypeMapper;

public abstract class BaseTable extends AbstractTable {
	private static final String TABLE = "TABLE";
	protected SchedulerQueryHandlerService provider;
	protected CalciteUseCase calcite;
	protected ApplicationEventPublisher eventPublisher;
	protected DagTypeMapper mapper = new DagTypeMapper();
	
	@SuppressWarnings("static-access")
	public BaseTable() {
		if(this.provider == null) {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			this.provider =  appCtx.getBean("schedulerQueryHandlerService", SchedulerQueryHandlerService.class);
			this.calcite =  appCtx.getBean("calciteService", CalciteUseCase.class);
			this.eventPublisher = appCtx.getBean("eventPublisher", ApplicationEventPublisher.class);
		}
	}
	
	protected List<Object[]> schemaForTable() throws DomainException{
		List<Object[]> list = new ArrayList<>();
		var availables = this.provider.availableJobs();
		for (Map.Entry<String, List<Map<String, String>>> entry : availables.entrySet()) {
			for (Map<String, String> operatormap : entry.getValue()) {
				String rdagname = operatormap.get("dagname");
				List<String> arr = this.calcite.getSchemas(rdagname);
				for (Iterator<String> iterator = arr.iterator(); iterator.hasNext();) {
					String schema1 = iterator.next();
					List<String> tables = this.calcite.getTables(schema1);
					for (Iterator<String> iterator2 = tables.iterator(); iterator2.hasNext();) {
						String table = iterator2.next();
						list.add(new Object[] {"SCH"+schema1,table,TABLE});
					}
				}
			}
		}
		return list;
	}
	protected List<Object[]> schemaForSchema() throws DomainException{
		List<Object[]> list = new ArrayList<>();
		var availables = this.provider.availableJobs();
		for (Map.Entry<String, List<Map<String, String>>> entry : availables.entrySet()) {
			for (Map<String, String> operatormap : entry.getValue()) {
				String rdagname = operatormap.get("dagname");
				List<String> arr = this.calcite.getSchemas(rdagname);
				for (Iterator<String> iterator = arr.iterator(); iterator.hasNext();) {
					String name = iterator.next();
					Object[] row = new Object[2];
					row[0] = "SCH"+name;
					var splitted = rdagname.split("\\.");
		        	row[1] = splitted[splitted.length-1];
					list.add(row);
				}
			}
		}
		return list;
	}
	protected List<Object[]> schemaForCatalog() throws DomainException{
		List<Object[]> list = new ArrayList<>();
		var availables = this.provider.availableJobs();
		for (Map.Entry<String, List<Map<String, String>>> entry : availables.entrySet()) {
			for (Map<String, String> operatormap : entry.getValue()) {
				Object[] row = new Object[1];
				String rdagname = operatormap.get("dagname");
	        	var splitted = rdagname.split("\\.");
	        	row[0] = splitted[splitted.length-1];
	        	list.add(row);
			}
		}
		return list;
	}
	protected List<Object[]> schemaForColumn() throws DomainException{
		List<Object[]> list = new ArrayList<>();
		var availables = this.provider.availableJobs();
		for (Map.Entry<String, List<Map<String, String>>> entry : availables.entrySet()) {
			for (Map<String, String> operatormap : entry.getValue()) {
				String rdagname = operatormap.get("dagname");
				List<String> arr = this.calcite.getSchemas(rdagname);
				for (Iterator<String> iterator = arr.iterator(); iterator.hasNext();) {
					String schema1 = iterator.next();
					List<String> tables = this.calcite.getTables(schema1);
					for (Iterator<String> iterator2 = tables.iterator(); iterator2.hasNext();) {
						String table = iterator2.next();
						List<Map<String,Object>> columns = this.calcite.getColumns(schema1,table);
						for (Iterator<Map<String,Object>> iterator3 = columns.iterator(); iterator3.hasNext();) {
							Map<String, Object> objects = iterator3.next();
							String typep = objects.get("type").toString().replace("class ", "");
							list.add(new Object[] {table,"TABLE",objects.get("name"),this.mapper.evaluate(typep),255,false});
						}
					}
				}
			}
		}
		return list;
	}
	protected List<Object[]> schemaForDag(String schema, String tableName) throws DomainException{
		List<Map<String,Object>> columns = this.calcite.getColumns(schema,tableName);
		List<Object[]> list = new ArrayList<>();
		Integer count = this.calcite.getCount("SCH"+schema,tableName);		
		for (int i = 0; i < count; i++) {
			var index = 0;
			var obj = new Object[columns.size()];
			for (Iterator<Map<String, Object>> iterator = columns.iterator(); iterator.hasNext();) {
				Map<String, Object> map = iterator.next();
				String columnName = map.get("name").toString();
				obj[index] = this.calcite.getCell("SCH"+schema,tableName,columnName,i);
				index++;	
			}
			list.add(obj);
			
		}
		return list;
	}
}
