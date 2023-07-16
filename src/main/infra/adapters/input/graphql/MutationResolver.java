package main.infra.adapters.input.graphql;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
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
			e.printStackTrace();
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
	public Status deleteGroupProperty(String token,String name,String group){
		try {
			handler.deleteGroupProperty(token, name,group);
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
	
	public Status saveUncompiled(String token, String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.saveUncompiled(token,defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public Status updateUncompiled(String token, Integer uncompiled ,String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.updateUncompiled(token, uncompiled, defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public Status compile(String token, Integer uncompiled,Integer force) {
		try {
			Boolean forceb = false;
			if(force != null) {
				forceb = force.equals(0)?false:true;	
			}
			
			handler.compile(token, uncompiled,forceb);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public Status deleteUncompiled(String token, Integer uncompiled) {
		try {
			handler.deleteUncompiled(token, uncompiled);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	
	public Status createAccount(String token,String username,String accountType,String pwdHash) {
		try {
			handler.createAccount(token,username,accountType,pwdHash);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public Status deleteAccount(String token,String username) {
		try {
			handler.deleteAccount(token,username);
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
	private JSONObject decodeBase64JSON(String base64EncodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        return new JSONObject(decodedString);
    }
}
