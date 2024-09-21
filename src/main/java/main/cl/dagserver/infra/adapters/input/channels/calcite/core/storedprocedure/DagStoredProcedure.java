package main.cl.dagserver.infra.adapters.input.channels.calcite.core.storedprocedure;

import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import main.cl.dagserver.application.ports.input.StageApiUsecase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

public class DagStoredProcedure {

    @SuppressWarnings("static-access")
	public static int run(String jarname,String dagname,String args) {
    	ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
    	StageApiUsecase api = appCtx.getBean("stageApiService", StageApiUsecase.class);
    	try {
    		args = args.trim().isEmpty()?"{}":args.trim();
    		var obj = new JSONObject(args);
			api.executeDag(jarname, dagname, obj);
			return 0;
		} catch (DomainException e) {
			e.printStackTrace();
			return -1;
		}
    	 
    }

}