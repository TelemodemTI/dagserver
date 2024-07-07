package main.cl.dagserver.domain.core;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import com.nhl.dflib.DataFrame;
import org.apache.log4j.Logger;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.repositories.SchedulerRepository;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
 
public abstract class OperatorStage implements Callable<DataFrame> {	
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;
	protected Properties args;
	protected Map<String,DataFrame> xcom;
	protected Properties optionals;
	
	public abstract DataFrame call() throws DomainException;
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
	public Properties getArgs() {
		return args;
	}
	public void setArgs(Properties args) {
		this.args = args;
	}
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
	public Map<String,DataFrame> getXcom() {
		return xcom;
	}
	public void setXcom(Map<String,DataFrame> xcom) {
		this.xcom = xcom;
	}
		
	
	public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class, String.class)).with(stepName, getClass(), propkey,optkey);
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
}
