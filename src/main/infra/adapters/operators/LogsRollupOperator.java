package main.infra.adapters.operators;

import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import main.domain.annotations.Operator;
import main.domain.repositories.SchedulerRepository;
import main.domain.types.OperatorStage;

@Operator(args={})
public class LogsRollupOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws Exception {		
		log.debug(this.getClass()+" init "+this.name);
		ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext().getServletContext());
		SchedulerRepository repo = new SchedulerRepository();
		AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
		factory.autowireBean( repo );
		factory.initializeBean( repo, "schedulerRepository" );
		
		var prop = new Properties();
		prop.load(springContext.getClassLoader().getResourceAsStream("application.properties"));
		
		Calendar rollup = Calendar.getInstance();
		rollup.setTimeInMillis(rollup.getTimeInMillis());
		rollup.add(Calendar.HOUR, Integer.parseInt(prop.getProperty("param.logs.rollup.hours")));
		repo.deleteLogsBy(rollup.getTime());
		log.debug(this.getClass()+" end "+this.name);
		return null;
	}

}
