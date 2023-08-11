package main.infra.adapters.operators;

import java.util.Properties;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.WebApplicationContextUtils;
import main.domain.annotations.Operator;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import main.infra.adapters.output.repositories.SchedulerRepository;
import net.bytebuddy.implementation.Implementation;

@Operator(args={})
public class RegisterSchedulerOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws DomainException {		
		try {
			log.debug(this.getClass()+" init "+this.name);
			var wa = ContextLoader.getCurrentWebApplicationContext();
			var prop = new Properties();
			var vl = (wa != null)?wa.getServletContext():null;
			if(wa != null && vl != null) {
				ApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(vl);
				SchedulerRepository repo = this.getSchedulerRepository(springContext);
				if(springContext != null) {
					var cls = springContext.getClassLoader();
					if(cls != null) {
						prop.load(cls.getResourceAsStream("application.properties"));
						repo.setMetadata(prop.getProperty("param.host"), prop.getProperty("param.name"));
						log.debug(this.getClass()+" end "+this.name);	
					}	
				}
			}
			return null;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
    	return null;
    }
	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}
	public String getIconImage() {
		return "internal.png";
	}
}
