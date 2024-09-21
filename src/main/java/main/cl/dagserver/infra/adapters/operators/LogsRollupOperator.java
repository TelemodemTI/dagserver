package main.cl.dagserver.infra.adapters.operators;

import java.util.Calendar;
import java.util.Properties;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={})
public class LogsRollupOperator extends OperatorStage {

	@SuppressWarnings({ "static-access" })
	@Override
	public DataFrame call() throws DomainException {		
		try {
			log.debug(this.getClass()+" init "+this.name);
			var prop = new Properties();
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			if(appCtx!=null) {
				var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
				var clsl = appCtx.getClassLoader();
				prop.load(clsl.getResourceAsStream("application.properties"));
				Calendar rollup = Calendar.getInstance();
				rollup.setTimeInMillis(rollup.getTimeInMillis());
				rollup.add(Calendar.HOUR, Integer.parseInt(prop.getProperty("param.logs.rollup.hours")));
				handler.deleteLogsBy(rollup.getTime());
				handler.deleteXCOM(rollup.getTime());
				log.debug(this.getClass()+" end "+this.name);	
			}
			return DataFrameUtils.createStatusFrame("ok");
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		return null;
	}
	
}
