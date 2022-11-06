package server.infra.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import server.application.handlers.SchedulerMutationHandler;


@Component
public class MutationResolver implements GraphQLMutationResolver {
	
	@Autowired
	SchedulerMutationHandler handler;
	
	public class Status {
		public String status;
	    public Integer code;
	    public String value;
	}
	
	public Status scheduleDag(String token,String dagname,String jarname) throws Exception {
		handler.scheduleDag(token,dagname,jarname);
		return ok();
    }
	public Status unscheduleDag(String token,String dagname,String jarname) throws Exception {
		handler.unscheduleDag(token,dagname, jarname);
		return ok();
	}
	
	private Status ok() {
		Status status = new Status();
		status.code = 200;
		status.status = "ok";
		status.value = "ok";
		return status;
	}
}
