package server.infra.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;

import server.application.handlers.SchedulerHandler;

@Component
public class MutationResolver implements GraphQLMutationResolver {
	
	@Autowired
	SchedulerHandler handler;
	
	public class Status {
		public String status;
	    public Integer code;
	    public String value;
	}
	
	public Status scheduleDag(String token,String dagname,String jarname) throws Exception {
		handler.scheduleDag(token,dagname,jarname);
		Status status = new Status();
		status.code = 200;
		status.status = "ok";
		status.value = "ok";
		return status;
    }
	public Status unscheduleDag(String token,String dagname,String jarname) throws Exception {
		handler.unscheduleDag(token,dagname, jarname);
		Status status = new Status();
		status.code = 200;
		status.status = "ok";
		status.value = "ok";
		return status;
	}
	
	public String getSchema() {
		return "";
	}
}
