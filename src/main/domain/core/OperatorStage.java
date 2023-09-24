package main.domain.core;

import java.util.Properties;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import main.domain.exceptions.DomainException;
import main.infra.adapters.output.repositories.SchedulerRepository;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
 
public abstract class OperatorStage {
	
	
    
	
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;

	
	
    public abstract JSONObject getMetadataOperator();
	
	
	public String getIconImage() {
		return "internal.png";
	}
		
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

	protected JarSchedulerAdapter getScheduler(ApplicationContext springContext) {
		JarSchedulerAdapter adapter = new JarSchedulerAdapter();
		AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
		factory.autowireBean( adapter );
		factory.initializeBean( adapter , "jarSchedulerAdapter" );
		return adapter;
	}
	
	
	public JSONObject getXcom() {
		return xcom;
	}

	public void setXcom(JSONObject xcom) {
		this.xcom = xcom;
	}
	
	
	
	
	public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class, String.class)).with(stepName, getClass(), propkey,optkey);
        } catch (Exception e) {
            throw new DomainException(e.getMessage());
        }
    }
}
