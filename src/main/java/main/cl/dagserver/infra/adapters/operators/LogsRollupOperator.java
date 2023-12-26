package main.cl.dagserver.infra.adapters.operators;

import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={})
public class LogsRollupOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws DomainException {		
		try {
			log.debug(this.getClass()+" init "+this.name);
			var prop = new Properties();
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			if(appCtx!=null) {
				var repo =  appCtx.getBean("schedulerRepository", SchedulerRepositoryOutputPort.class);
				var scheduler = appCtx.getBean("jarSchedulerAdapter",JarSchedulerOutputPort.class);
				var clsl = appCtx.getClassLoader();
				prop.load(clsl.getResourceAsStream("application.properties"));
				Calendar rollup = Calendar.getInstance();
				rollup.setTimeInMillis(rollup.getTimeInMillis());
				rollup.add(Calendar.HOUR, Integer.parseInt(prop.getProperty("param.logs.rollup.hours")));
				repo.deleteLogsBy(rollup.getTime());
				scheduler.deleteXCOM(rollup.getTime());
				log.debug(this.getClass()+" end "+this.name);	
			}
			return null;	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}
	
}
