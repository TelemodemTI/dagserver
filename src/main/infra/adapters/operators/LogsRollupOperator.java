package main.infra.adapters.operators;

import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;
import main.domain.annotations.Operator;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import main.infra.adapters.output.repositories.SchedulerRepository;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;


@Operator(args={})
public class LogsRollupOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws DomainException {		
		try {
			log.debug(this.getClass()+" init "+this.name);
			var prop = new Properties();
			var ctx = ContextLoader.getCurrentWebApplicationContext();
			if(ctx != null) {
				var srv = ctx.getServletContext();
				if(srv != null) {
					ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(srv);
					SchedulerRepository repo = this.getSchedulerRepository(springContext);
					JarSchedulerAdapter scheduler = this.getScheduler(springContext);
					var clsl = (springContext!=null)? springContext.getClassLoader():null;
					if(clsl!=null) {
						prop.load(clsl.getResourceAsStream("application.properties"));
						Calendar rollup = Calendar.getInstance();
						rollup.setTimeInMillis(rollup.getTimeInMillis());
						rollup.add(Calendar.HOUR, Integer.parseInt(prop.getProperty("param.logs.rollup.hours")));
						repo.deleteLogsBy(rollup.getTime());
						scheduler.deleteXCOM(rollup.getTime());
						log.debug(this.getClass()+" end "+this.name);	
					}
				} else return null;
			}
			return null;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}
	
}
