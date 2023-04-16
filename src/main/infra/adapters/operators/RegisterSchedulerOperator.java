package main.infra.adapters.operators;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import main.domain.annotations.Operator;
import main.domain.repositories.SchedulerRepository;
import main.domain.types.OperatorStage;

@Operator(args={})
public class RegisterSchedulerOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws Exception {		
		log.debug(this.getClass()+" init "+this.name);
		ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ContextLoaderListener.getCurrentWebApplicationContext().getServletContext());
		SchedulerRepository repo = this.getSchedulerRepository(springContext);
		var prop = new Properties();
		prop.load(springContext.getClassLoader().getResourceAsStream("application.properties"));
		repo.setMetadata(prop.getProperty("param.host"), prop.getProperty("param.name"));
		log.debug(this.getClass()+" end "+this.name);
		return null;
	}

}
