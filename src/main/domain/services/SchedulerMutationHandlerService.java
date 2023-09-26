package main.domain.services;

import java.util.Map;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.input.SchedulerMutationUseCase;
import main.domain.core.BaseServiceComponent;
import main.domain.exceptions.DomainException;

@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerMutationHandlerService extends BaseServiceComponent implements SchedulerMutationUseCase {
	
	private static final String CLAIMS = "claims";
	private static final String ADMIN = "ADMIN";
	private static final String JARNAME = "jarname";
	private static final String TYPEACCOUNT = "typeAccount";
	
	@Value( "${param.git_hub.propkey}" )
	private String gitHubPropkey;
	
	@Value( "${param.rabbit.propkey}" )
	private String rabbitPropkey;
	
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SchedulerMutationHandlerService.class);
	
	
	
	@Override
	public void scheduleDag(String token, String dagname,String jarname) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			scanner.init().scheduler(dagname,jarname);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			scanner.init().unschedule(dagname,jarname);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void createProperty(String token, String name, String description, String value,String group) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.setProperty(name,description,value,group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteProperty(String token, String name,String group) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.delProperty(name,group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void execute(String token, String jarname, String dagname, String source) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			scanner.init().execute(jarname, dagname,source);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void saveUncompiled(String token, JSONObject json) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.addUncompiled(json.getString(JARNAME),json);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.updateUncompiled(uncompiled,json);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void compile(String token, Integer uncompiled, Boolean force) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			String bin = repository.getUncompiledBin(uncompiled);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString(JARNAME);
			compiler.createJar(bin,force);
			repository.createParams(jarname,bin);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteUncompiled(String token, Integer uncompiled) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.deleteUncompiled(uncompiled);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteGroupProperty(String token, String name, String group) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.delGroupProperty(group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	public void createAccount(String token, String username, String accountType, String pwdHash) throws DomainException {
		try {
			var claims = tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
			if(claimsmap.get(TYPEACCOUNT).equals(ADMIN)) {
				if(repository.findUser(username).isEmpty()) {
					repository.createAccount(username,accountType,pwdHash);	
				} else {
					throw new DomainException("account already exists");
				}
			} else {
				throw new DomainException("insufficient privileges");
			}	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	@SuppressWarnings("unchecked")
	public void deleteAccount(String token, String username) throws DomainException {
		try {
			var claims = tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
			if(claimsmap.get(TYPEACCOUNT).equals(ADMIN)) {
				repository.delAccount(username);
			} else {
				throw new DomainException("insufficient privileges");
			}	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void updateParamsCompiled(String token,String idope, String typeope, String jarname, String bin) throws DomainException  {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.updateParams(idope, typeope, jarname, bin);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void updateProp(String token, String group, String key, String value) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.updateprop(group,key,value);
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
	@Override
	@SuppressWarnings("unchecked")
	public void deleteJarfile(String token, String jarname) throws DomainException {
		try {
			var claims = tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
			if(claimsmap.get(TYPEACCOUNT).equals(ADMIN)) {
				compiler.deleteJarfile(jarname);
			} else {
				throw new DomainException("unauthorized");
			}
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public void addGitHubWebhook(String token, String name, String repositoryUrl, String secret, String dagname,String jarname) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.setProperty(name,repositoryUrl,secret,this.gitHubPropkey);
			repository.setProperty("dagname", "GENERATED", dagname, name);
			repository.setProperty(JARNAME, "GENERATED", jarname, name);
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
	@Override
	public void removeGithubWebhook(String token, String name) throws DomainException {
		try {
			tokenEngine.untokenize(token, jwtSecret, jwtSigner);
			repository.delProperty(name, this.gitHubPropkey);
			repository.delProperty("dagname", name);
			repository.delProperty(JARNAME, name);
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteLog(String token, Integer logid) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.deleteLog(logid);
	}
	@Override
	public void deleteAllLogs(String token, String dagname) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.deleteAllLogs(dagname);
		
	}
	@Override
	public void renameUncompiled(String token, Integer uncompiled, String newname) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.renameUncompiled(uncompiled,newname);
	}
	@Override
	public void saveRabbitChannel(String token, String host, String user, String pwd, Integer port)
			throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.setProperty("host", "GENERATED", host, rabbitPropkey );
		repository.setProperty("username", "GENERATED", user, rabbitPropkey );
		if(!pwd.equals("******")) {
			repository.setProperty("password", "GENERATED", pwd, rabbitPropkey );	
		}
		repository.setProperty("port", "GENERATED", port.toString(), rabbitPropkey );
		repository.setProperty("STATUS", "rabbit channel status", "ACTIVE", rabbitPropkey );
	}
	@Override
	public void addQueue(String token, String queue, String jarfile, String dagname) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.setProperty(queue , "GENERATED", "rabbit_consumer_queue" , rabbitPropkey );
		repository.setProperty("dagname", "GENERATED", dagname, queue);
		repository.setProperty(JARNAME, "GENERATED", jarfile, queue);
	}
	@Override
	public void delQueue(String token, String queue) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		repository.delProperty(queue, rabbitPropkey);
		repository.delGroupProperty(queue);
	}
	@Override
	public void saveRedisChannel(String token, String mode, String hostport, String channel, String jarfile, String dagname) throws DomainException {
		tokenEngine.untokenize(token, jwtSecret, jwtSigner);
		Boolean bmode = Boolean.parseBoolean(mode);
		if(bmode) {
			String[] hosallarr = hostport.split(";");
			for (int i = 0; i < hosallarr.length; i++) {
				String string = hosallarr[i];
				String[] hosparr = string.split(":");
				repository.setProperty("hostname", "GENERATED", hosparr[0], redisPropkey );
				repository.setProperty("port", "GENERATED", hosparr[1], redisPropkey );
			}
			repository.setProperty("channel", "GENERATED", channel, redisPropkey );
			repository.setProperty("dagname", "GENERATED", dagname, channel);
			repository.setProperty(JARNAME, "GENERATED", jarfile, channel);
			repository.setProperty("STATUS", "rabbit channel status", "ACTIVE", rabbitPropkey );
		} else {
			String[] hosparr = hostport.split(":");
			repository.setProperty("hostname", "GENERATED", hosparr[0], redisPropkey );
			repository.setProperty("port", "GENERATED", hosparr[1], redisPropkey );
			repository.setProperty("channel", "GENERATED", channel, redisPropkey );
			repository.setProperty("dagname", "GENERATED", dagname, channel);
			repository.setProperty(JARNAME, "GENERATED", jarfile, channel);
			repository.setProperty("STATUS", "rabbit channel status", "ACTIVE", rabbitPropkey );
		}
	}
	
	
}
