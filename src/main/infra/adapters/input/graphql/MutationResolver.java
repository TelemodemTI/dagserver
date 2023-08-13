package main.infra.adapters.input.graphql;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import main.application.ports.input.SchedulerMutationUseCase;
import main.domain.exceptions.DomainException;



@Component
public class MutationResolver implements GraphQLMutationResolver {
	
	private static final Logger logger = Logger.getLogger(MutationResolver.class);
	
	@Autowired
	SchedulerMutationUseCase handler;
	
	public class Status {
		private String status;
	    private Integer code;
	    private String value;
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public Integer getCode() {
			return code;
		}
		public void setCode(Integer code) {
			this.code = code;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	    
	}
	
	public Status scheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			handler.scheduleDag(token,dagname,jarname);
			return ok();	
		} catch (Exception e) {
			logger.error(e);
			return error(e);
		}
    }
	public Status unscheduleDag(String token,String dagname,String jarname) throws DomainException {
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
			if(force != null && !force.equals(0)) {
				forceb = true;	
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

	public Status updateParamsCompiled(String token, String idope, String typeope, String jarname, String bin) {
		try {
			handler.updateParamsCompiled(token, idope, typeope, jarname, bin);
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
