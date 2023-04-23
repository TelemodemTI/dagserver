package main.infra.adapters.input.graphql;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import main.application.ports.input.SchedulerMutationUseCase;


@Component
public class MutationResolver implements GraphQLMutationResolver {
	
	@Autowired
	SchedulerMutationUseCase handler;
	
	public class Status {
		public String status;
	    public Integer code;
	    public String value;
	}
	
	public Status scheduleDag(String token,String dagname,String jarname) throws Exception {
		try {
			handler.scheduleDag(token,dagname,jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
    }
	public Status unscheduleDag(String token,String dagname,String jarname) throws Exception {
		try {
			handler.unscheduleDag(token,dagname, jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
		
	}
	
	public Status createProperty(String token, String name, String description, String value,String group) {
		try {
			handler.createProperty(token, name, description, value,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public Status deleteProperty(String token,String name,String group){
		try {
			handler.deleteProperty(token, name,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public Status executeDag(String token,String dagname,String jarname) {
		try {
			handler.execute(token,jarname, dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	private Status ok() {
		Status status = new Status();
		status.code = 200;
		status.status = "ok";
		status.value = "ok";
		return status;
	}
	private Status error(Exception e) {
		Status status = new Status();
		status.code = 503;
		status.status = "error";
		status.value = ExceptionUtils.getRootCauseMessage(e);
		return status;
	}
}
