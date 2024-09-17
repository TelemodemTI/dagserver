package main.cl.dagserver.infra.adapters.input.graphql;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;

import main.cl.dagserver.application.ports.input.SchedulerMutationUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
@CrossOrigin(origins = "*",methods={RequestMethod.GET,RequestMethod.POST})
public class MutationResolver {

	private SchedulerMutationUseCase handler;
	
	@Autowired
	public MutationResolver(SchedulerMutationUseCase handler) {
		this.handler = handler;
	}
	
	@MutationMapping
	public StatusOp logout(@Argument String token) {
		try {
			handler.logout(token);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp scheduleDag(@Argument String token,@Argument String dagname,@Argument String jarname) throws DomainException {
		try {
			handler.scheduleDag(token,dagname,jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
    }
	@MutationMapping
	public StatusOp unscheduleDag(@Argument String token,@Argument String dagname,@Argument String jarname) throws DomainException {
		try {
			handler.unscheduleDag(token,dagname, jarname);
			return ok();	
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp createProperty(@Argument String token,@Argument String name,@Argument String description,@Argument String value,@Argument String group) {
		try {
			handler.createProperty(token, name, description, value,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteProperty(@Argument String token,@Argument String name,@Argument String group){
		try {
			handler.deleteProperty(token, name,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteGroupProperty(@Argument String token,@Argument String name,@Argument String group){
		try {
			handler.deleteGroupProperty(token, name,group);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp executeDag(@Argument String token,@Argument String dagname,@Argument String jarname,@Argument String data) {
		try {
			handler.execute(token,jarname, dagname,"GRAPHQL_ENDPOINT",data);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp saveUncompiled(@Argument String token,@Argument String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.saveUncompiled(token,defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp updateUncompiled(@Argument String token,@Argument Integer uncompiled ,@Argument String bin) {
		try {
			var defobj = this.decodeBase64JSON(bin);
			handler.updateUncompiled(token, uncompiled, defobj);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp compile(@Argument String token,@Argument Integer uncompiled,@Argument Integer force) {
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
	@MutationMapping
	public StatusOp deleteUncompiled(@Argument String token, @Argument Integer uncompiled) {
		try {
			handler.deleteUncompiled(token, uncompiled);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp createAccount(@Argument String token,@Argument String username,@Argument String accountType,@Argument String pwdHash) {
		try {
			handler.createAccount(token,username,accountType,pwdHash);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteAccount(@Argument String token,@Argument String username) {
		try {
			handler.deleteAccount(token,username);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp updateParamsCompiled(@Argument String token,@Argument  String idope,@Argument  String typeope, @Argument String jarname, @Argument String bin) {
		try {
			handler.updateParamsCompiled(token, idope, typeope, jarname, bin);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp updateProp(@Argument String token,@Argument String group,@Argument String key,@Argument String value) {
		try {
			handler.updateProp(token,group,key,value);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteJarfile(@Argument String token,@Argument  String jarname) {
		try {
			handler.deleteJarfile(token,jarname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteLog(@Argument String token,@Argument Integer logid) {
		try {
			handler.deleteLog(token,logid);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteAllLogs(@Argument String token,@Argument  String dagname) {
		try {
			handler.deleteAllLogs(token,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp renameUncompiled(@Argument String token,@Argument Integer uncompiled,@Argument String newname) {
		try {
			handler.renameUncompiled(token,uncompiled,newname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp saveRabbitChannel(@Argument String token,@Argument  String host,@Argument  String user,@Argument  String pwd,@Argument  Integer port) {
		try {
			handler.saveRabbitChannel(token, host, user, pwd, port);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp addQueue(@Argument String token, @Argument String queue, @Argument String jarfile, @Argument String dagname) {
		try {
			handler.addQueue(token, queue, jarfile , dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp delQueue(@Argument String token,@Argument  String queue) {
		try {
			handler.delQueue(token, queue);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp saveRedisChannel(@Argument String token,@Argument String mode,@Argument String hostnames,@Argument String portnumbers) {
		try {
			handler.saveRedisChannel(token, mode,hostnames,portnumbers);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp addListener(@Argument String token,@Argument  String channel,@Argument String jarfile,@Argument String dagname) {
		try {
			handler.addListener(token, channel,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp delListener(@Argument String token,@Argument String channel) {
		try {
			handler.delListener(token, channel);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp saveKafkaChannel(@Argument String token,@Argument String bootstrapServers,@Argument String groupId,@Argument Integer poll) {
		try {
			handler.saveKafkaChannel(token, bootstrapServers,groupId,poll);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp addConsumer(@Argument String token,@Argument  String topic,@Argument String jarfile,@Argument String dagname) {
		try {
			handler.addConsumer(token, topic,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp delConsumer(@Argument String token,@Argument String topic) {
		try {
			handler.delConsumer(token, topic);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp saveActiveMQChannel(@Argument String token,@Argument String host,@Argument String user,@Argument String pwd) {
		try {
			handler.saveActiveMQChannel(token, host,user,pwd);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp addConsumerAM(@Argument String token,@Argument  String queue,@Argument  String jarfile,@Argument  String dagname) {
		try {
			handler.addConsumerAM(token, queue,jarfile,dagname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp delConsumerAM(@Argument String token,@Argument String queue) {
		try {
			handler.delConsumerAM(token, queue);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp removeException(@Argument String token,@Argument String eventDt){
		try {
			handler.removeException(token, eventDt);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp reimport(@Argument String token,@Argument String jarname) {
		try {
			handler.reimport(token, jarname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp createFolder(@Argument String token,@Argument String foldername) {
		try {
			handler.createFolder(token, foldername);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteFile(@Argument String token,@Argument String folder,@Argument String file) {
		try {
			handler.deleteFile(token, folder,file);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp copyFile(@Argument String token,@Argument String filename,@Argument String copyname) {
		try {
			handler.copyFile(token, filename,copyname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp moveFile(@Argument String token,@Argument String folder,@Argument String filename,@Argument String newpath) {
		try {
			handler.moveFile(token,folder, filename,newpath);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp createApiKey(@Argument String token,@Argument String appname) {
		try {
			handler.createApiKey(token,appname);
			return ok();
		} catch (Exception e) {
			return error(e);
		}
	}
	@MutationMapping
	public StatusOp deleteApiKey(@Argument String token,@Argument String appname) {
		try {
			handler.deleteApiKey(token,appname);
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
