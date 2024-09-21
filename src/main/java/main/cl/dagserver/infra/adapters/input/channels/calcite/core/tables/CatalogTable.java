package main.cl.dagserver.infra.adapters.input.channels.calcite.core.tables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.calcite.DataContext;
import org.apache.calcite.linq4j.Enumerable;
import org.apache.calcite.linq4j.Linq4j;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.schema.ScannableTable;
import org.apache.calcite.schema.impl.AbstractTable;
import org.apache.calcite.sql.type.SqlTypeName;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.context.ApplicationContext;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.domain.services.SchedulerQueryHandlerService;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Log4j2
public class CatalogTable extends AbstractTable implements ScannableTable {

	private SchedulerQueryHandlerService provider;
	
	@Override
	public RelDataType getRowType(RelDataTypeFactory typeFactory) {
		RelDataTypeFactory.Builder builder = typeFactory.builder();
		builder.add("TABLE_CAT", SqlTypeName.VARCHAR);
		return builder.build();
	}

	@SuppressWarnings("static-access")
	@Override
	public Enumerable<@Nullable Object[]> scan(DataContext root) {
		List<Object[]> list = new ArrayList<>();
		if(this.provider == null) {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			this.provider =  appCtx.getBean("schedulerQueryHandlerService", SchedulerQueryHandlerService.class);	
		}
		try {
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
		} catch (Exception e) {
			log.error(e);
		}
		Object[] row = new Object[1];
		row[0] = "DEFAULT";
		list.add(row);
		return Linq4j.asEnumerable(list);
	}
}
