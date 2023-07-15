package main.infra.adapters.input.graphql.types;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import main.infra.adapters.output.repositories.SchedulerRepository;
import net.bytebuddy.implementation.Implementation;

public abstract class OperatorStage {
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;
	
	public abstract Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws Exception;
	public abstract JSONObject getMetadataOperator();
	public abstract String getIconImage() throws Exception;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected Properties args;
	protected JSONObject xcom = new JSONObject();

	
	public Properties getArgs() {
		return args;
	}

	public void setArgs(Properties args) {
		this.args = args;
	}

	
	protected Properties optionals;
	public Properties getOptionals() {
		return optionals;
	}

	public void setOptionals(Properties optionals) {
		this.optionals = optionals;
	}
	protected SchedulerRepository getSchedulerRepository(ApplicationContext springContext) {
		SchedulerRepository repo = new SchedulerRepository();
		AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
		factory.autowireBean( repo );
		factory.initializeBean( repo, "schedulerRepository" );
		return repo;
	}

	public JSONObject getXcom() {
		return xcom;
	}

	public void setXcom(JSONObject xcom) {
		this.xcom = xcom;
	}
}
