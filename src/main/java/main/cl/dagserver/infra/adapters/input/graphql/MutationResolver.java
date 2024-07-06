package main.cl.dagserver.infra.adapters.input.graphql;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import main.cl.dagserver.application.ports.input.SchedulerMutationUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;



@Component

public class MutationResolver implements GraphQLMutationResolver {

	private SchedulerMutationUseCase handler;
	
	@Autowired
	public MutationResolver(SchedulerMutationUseCase handler) {
		this.handler = handler;
	}
	
	public StatusOp scheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			handler.scheduleDag(token,dagname,jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
    }
	public StatusOp unscheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			handler.unscheduleDag(token,dagname, jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp createProperty(String token, String name, String description, String value,String group) {
		try {
			handler.createProperty(token, name, description, value,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp deleteProperty(String token,String name,String group){
		try {
			handler.deleteProperty(token, name,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp deleteGroupProperty(String token,String name,String group){
		try {
			handler.deleteGroupProperty(token, name,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp executeDag(String token,String dagname,String jarname,String data) {
		try {
			handler.execute(token,jarname, dagname,"GRAPHQL_ENDPOINT",data);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp saveUncompiled(String token, String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.saveUncompiled(token,defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp updateUncompiled(String token, Integer uncompiled ,String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.updateUncompiled(token, uncompiled, defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp compile(String token, Integer uncompiled,Integer force) {
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
	public StatusOp deleteUncompiled(String token, Integer uncompiled) {
		try {
			handler.deleteUncompiled(token, uncompiled);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	
	public StatusOp createAccount(String token,String username,String accountType,String pwdHash) {
		try {
			handler.createAccount(token,username,accountType,pwdHash);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp deleteAccount(String token,String username) {
		try {
			handler.deleteAccount(token,username);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}

	public StatusOp updateParamsCompiled(String token, String idope, String typeope, String jarname, String bin) {
		try {
			handler.updateParamsCompiled(token, idope, typeope, jarname, bin);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp updateProp(String token,String group,String key,String value) {
		try {
			handler.updateProp(token,group,key,value);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	
	public StatusOp deleteJarfile(String token, String jarname) {
		try {
			handler.deleteJarfile(token,jarname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}

	public StatusOp addGitHubWebhook(String token,String name,String repository,String secret,String dagname, String jarname) {
		try {
			handler.addGitHubWebhook(token,name,repository,secret,dagname,jarname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp removeGithubWebhook(String token, String name) {
		try {
			handler.removeGithubWebhook(token,name);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp deleteLog(String token, Integer logid) {
		try {
			handler.deleteLog(token,logid);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp deleteAllLogs(String token, String dagname) {
		try {
			handler.deleteAllLogs(token,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp renameUncompiled(String token,Integer uncompiled,String newname) {
		try {
			handler.renameUncompiled(token,uncompiled,newname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp saveRabbitChannel(String token, String host, String user, String pwd, Integer port) {
		try {
			handler.saveRabbitChannel(token, host, user, pwd, port);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp addQueue(String token, String queue, String jarfile, String dagname) {
		try {
			handler.addQueue(token, queue, jarfile , dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp delQueue(String token, String queue) {
		try {
			handler.delQueue(token, queue);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp saveRedisChannel(String token,String mode,String hostnames,String portnumbers) {
		try {
			handler.saveRedisChannel(token, mode,hostnames,portnumbers);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp addListener(String token, String channel,String jarfile,String dagname) {
		try {
			handler.addListener(token, channel,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp delListener(String token,String channel) {
		try {
			handler.delListener(token, channel);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	
	public StatusOp saveKafkaChannel(String token,String bootstrapServers,String groupId,Integer poll) {
		try {
			handler.saveKafkaChannel(token, bootstrapServers,groupId,poll);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp addConsumer(String token, String topic,String jarfile,String dagname) {
		try {
			handler.addConsumer(token, topic,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp delConsumer(String token,String topic) {
		try {
			handler.delConsumer(token, topic);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp saveActiveMQChannel(String token,String host,String user,String pwd) {
		try {
			handler.saveActiveMQChannel(token, host,user,pwd);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	
	public StatusOp addConsumerAM(String token, String queue, String jarfile, String dagname) {
		try {
			handler.addConsumerAM(token, queue,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp delConsumerAM(String token,String queue) {
		try {
			handler.delConsumerAM(token, queue);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp removeException(String token,String eventDt){
		try {
			handler.removeException(token, eventDt);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	public StatusOp reimport(String token,String jarname) {
		try {
			handler.reimport(token, jarname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	private StatusOp ok() {
		StatusOp status = new StatusOp();
		status.setCode(200);
		status.setStatus("ok");
		status.setValue("ok");
		return status;
	}
	private StatusOp error(Exception e) {
		StatusOp status = new StatusOp();
		status.setCode(503);
		status.setStatus("error");
		status.setValue(ExceptionUtils.getRootCauseMessage(e));
		return status;
	}
	private JSONObject decodeBase64JSON(String base64EncodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
        String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
        return new JSONObject(decodedString);
    }
}
