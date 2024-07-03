package main.cl.dagserver.infra.adapters.operators;

import java.util.Properties;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={})
public class RegisterSchedulerOperator extends OperatorStage {

	@SuppressWarnings({ "static-access" })
	@Override
	public DataFrame call() throws DomainException {		
		try {
			log.debug(this.getClass()+" init "+this.name);
			var prop = new Properties();
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			if(appCtx != null) {
				var repo =  appCtx.getBean("schedulerRepository", SchedulerRepositoryOutputPort.class);
				var cls = appCtx.getClassLoader();
				prop.load(cls.getResourceAsStream("application.properties"));
				repo.setMetadata(prop.getProperty("param.host"), prop.getProperty("param.name"));
				log.debug(this.getClass()+" end "+this.name);	
			}	
			return createStatusFrame("ok");
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}

}
