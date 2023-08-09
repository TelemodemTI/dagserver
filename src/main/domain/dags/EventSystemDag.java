package main.domain.dags;

import main.domain.core.DagExecutable;
import main.infra.adapters.operators.Junit5SuiteOperator;
import java.util.Properties;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;

import main.domain.annotations.Dag;


@Dag(name = "event_system_dag", group="system_dags", onEnd="background_system_dag")
public class EventSystemDag extends DagExecutable {

	public EventSystemDag() throws Exception {
		super();
		ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(ContextLoader.getCurrentWebApplicationContext().getServletContext());
		var prop = new Properties();
		prop.load(springContext.getClassLoader().getResourceAsStream("application.properties"));
		Boolean localTest = Boolean.parseBoolean(prop.getProperty("param.junit.local"));	
		if(localTest) {
			var propop = new Properties();
			propop.setProperty("suiteClass", "main.BasicTest");
			this.addOperator("local_testing",Junit5SuiteOperator.class,propop);	
		}
	}
	
}
