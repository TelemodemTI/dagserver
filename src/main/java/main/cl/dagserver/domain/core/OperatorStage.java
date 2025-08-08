package main.cl.dagserver.domain.core;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import com.nhl.dflib.DataFrame;

import lombok.Data;

import org.apache.log4j.Logger;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.repositories.SchedulerRepository;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
 
@Data
public abstract class OperatorStage implements Callable<DataFrame> {	
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;
	protected Properties args;
	protected Map<String,DataFrame> xcom;
	protected Properties optionals;
	
	public abstract DataFrame call() throws DomainException;
    public abstract JSONObject getMetadataOperator();
    

	public String getOptionalProperty(String key) {
		String value = this.optionals.getProperty(key);
		if(value.startsWith("${") && value.endsWith("}")) {
			String xcomheader = value.replace("${", "").replace("}", "");
			if(this.xcom.containsKey(xcomheader)) {
				DataFrame df = (DataFrame) this.xcom.get(xcomheader);
				value = df.getColumn("output").get(0).toString();
			}
		}
		return value;
	}
    
	public String getInputProperty(String key) {
		String value = this.args.getProperty(key);
		if(value.startsWith("${") && value.endsWith("}")) {
			String xcomheader = value.replace("${", "").replace("}", "");
			if(this.xcom.containsKey(xcomheader)) {
				DataFrame df = (DataFrame) this.xcom.get(xcomheader);
				value = df.getColumn("output").get(0).toString();
			}
		}
		return value;
	}

	public String getIconImage() {
		return "internal.png";
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
	public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class, String.class)).with(stepName, getClass(), propkey,optkey);
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
	
}
