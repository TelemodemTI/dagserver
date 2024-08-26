package main.cl.dagserver.domain.services;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.input.SchedulerMutationUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;

@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerMutationHandlerService extends BaseServiceComponent implements SchedulerMutationUseCase {

	private static final String JARNAME = "jarname";
	private static final String DAGNAME = "dagname";
	private static final String GENERATED = "GENERATED";
	private static final String STATUS = "STATUS";
	private static final String ACTIVE = "ACTIVE";
	
	@Value( "${param.git_hub.propkey}" )
	private String gitHubPropkey;
	
	@Value( "${param.rabbit.propkey}" )
	private String rabbitPropkey;
	
	@Value( "${param.redis.propkey}" )
	private String redisPropkey;
	
	@Value( "${param.kafka.propkey}" )
	private String kafkaPropkey;
	
	@Value( "${param.activemq.propkey}" )
	private String activeMQPropkey;
	
	
	@Override
	public void scheduleDag(String token, String dagname,String jarname) throws DomainException {
		auth.untokenize(token);
		scanner.init().scheduler(dagname,jarname);	
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			auth.untokenize(token);
			scanner.init().unschedule(dagname,jarname);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void createProperty(String token, String name, String description, String value,String group) throws DomainException {
		try {
			auth.untokenize(token);
			repository.setProperty(name,description,value,group);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void deleteProperty(String token, String name,String group) throws DomainException {
		try {
			auth.untokenize(token);
			repository.delProperty(name,group);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void execute(String token, String jarname, String dagname, String source,String data) throws DomainException {
		try {
			auth.untokenize(token);
			scanner.init().execute(jarname, dagname,source,data);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void saveUncompiled(String token, JSONObject json) throws DomainException {
		try {
			auth.untokenize(token);
			repository.addUncompiled(json.getString(JARNAME),json);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws DomainException {
		try {
			auth.untokenize(token);
			repository.updateUncompiled(uncompiled,json);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void compile(String token, Integer uncompiled, Boolean force) throws DomainException {
		try {
			auth.untokenize(token);
			String bin = repository.getUncompiledBin(uncompiled);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString(JARNAME);
			List<String> groups = repository.createParams(jarname,bin);
			Properties prop = new Properties();
			for (Iterator<String> iterator = groups.iterator(); iterator.hasNext();) {
				String string = iterator.next();
				var propitem = repository.getProperties(string);
				this.convertToProperties(prop,propitem);
			}
			compiler.createJar(bin,force,prop);
				
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void deleteUncompiled(String token, Integer uncompiled) throws DomainException {
		try {
			auth.untokenize(token);
			repository.deleteUncompiled(uncompiled);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void deleteGroupProperty(String token, String name, String group) throws DomainException {
		try {
			auth.untokenize(token);
			repository.delGroupProperty(group);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void createAccount(String token, String username, String accountType, String pwdHash) throws DomainException {
		try {
			var claims = auth.untokenize(token);
			if(claims.getAccountType().equals(AccountType.ADMIN)) {
				if(repository.findUser(username).isEmpty()) {
					repository.createAccount(username,accountType,pwdHash);	
				} else {
					throw new DomainException(new Exception("account already exists"));
				}
			} else {
				throw new DomainException(new Exception("insufficient privileges"));
			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void deleteAccount(String token, String username) throws DomainException {
		try {
			var claims = auth.untokenize(token);
			if(claims.getAccountType().equals(AccountType.ADMIN)) {
				repository.delAccount(username);
			} else {
				throw new DomainException(new Exception("insufficient privileges"));
			}	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void updateParamsCompiled(String token,String idope, String typeope, String jarname, String bin) throws DomainException  {
		try {
			auth.untokenize(token);
			repository.updateParams(idope, typeope, jarname, bin);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void updateProp(String token, String group, String key, String value) throws DomainException {
		try {
			auth.untokenize(token);
			repository.updateprop(group,key,value);
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	@Override
	public void deleteJarfile(String token, String jarname) throws DomainException {
		try {
			var claims = auth.untokenize(token);
			if(claims.getAccountType().equals(AccountType.ADMIN)) {
				compiler.deleteJarfile(jarname);
			} else {
				throw new DomainException(new Exception("unauthorized"));
			}
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	@Override
	public void addGitHubWebhook(String token, String name, String repositoryUrl, String secret, String dagname,String jarname) throws DomainException {
		try {
			auth.untokenize(token);
			repository.setProperty(name,repositoryUrl,secret,this.gitHubPropkey);
			repository.setProperty(DAGNAME, GENERATED, dagname, name);
			repository.setProperty(JARNAME, GENERATED, jarname, name);
			repository.setProperty(STATUS, "github channel status", ACTIVE, "GITHUB_WEBHOOK_PROPS");
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	@Override
	public void removeGithubWebhook(String token, String name) throws DomainException {
		try {
			auth.untokenize(token);
			repository.delProperty(name, this.gitHubPropkey);
			repository.delProperty(DAGNAME, name);
			repository.delProperty(JARNAME, name);
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	@Override
	public void deleteLog(String token, Integer logid) throws DomainException {
		auth.untokenize(token);
		repository.deleteLog(logid);
	}
	@Override
	public void deleteAllLogs(String token, String dagname) throws DomainException {
		auth.untokenize(token);
		repository.deleteAllLogs(dagname);
		
	}
	@Override
	public void renameUncompiled(String token, Integer uncompiled, String newname) throws DomainException {
		auth.untokenize(token);
		repository.renameUncompiled(uncompiled,newname);
	}
	@Override
	public void saveRabbitChannel(String token, String host, String user, String pwd, Integer port)
			throws DomainException {
		auth.untokenize(token);
		repository.setProperty("host", GENERATED, host, rabbitPropkey );
		repository.setProperty("username", GENERATED, user, rabbitPropkey );
		if(!pwd.equals("******")) {
			repository.setProperty("password", GENERATED, pwd, rabbitPropkey );	
		}
		repository.setProperty("port", GENERATED, port.toString(), rabbitPropkey );
		repository.setProperty(STATUS, "rabbit channel status", ACTIVE, rabbitPropkey );
	}
	@Override
	public void addQueue(String token, String queue, String jarfile, String dagname) throws DomainException {
		auth.untokenize(token);
		repository.setProperty(queue , GENERATED, "rabbit_consumer_queue" , rabbitPropkey );
		repository.setProperty(DAGNAME, GENERATED, dagname, queue);
		repository.setProperty(JARNAME, GENERATED, jarfile, queue);
	}
	@Override
	public void delQueue(String token, String queue) throws DomainException {
		auth.untokenize(token);
		repository.delProperty(queue, rabbitPropkey);
		repository.delGroupProperty(queue);
	}
	@Override
	public void saveRedisChannel(String token, String mode, String hostnames, String portnumbers) throws DomainException {
		auth.untokenize(token);
		Boolean bmode = Boolean.parseBoolean(mode);
		String[] hosallarr = hostnames.split(";");
		String[] portnumbersarr = portnumbers.split(";");
		for (int i = 0; i < hosallarr.length; i++) {
			String string = hosallarr[i];
			String portnumber = portnumbersarr[i];
			repository.setProperty("hostname", GENERATED, string, redisPropkey );
			repository.setProperty("port", GENERATED, portnumber, redisPropkey );
		}
		repository.setProperty("MODE", GENERATED, bmode.toString(), redisPropkey );
		repository.setProperty(STATUS, "rabbit channel status", ACTIVE, redisPropkey );
	}
	@Override
	public void addListener(String token, String listener, String jarfile, String dagname) throws DomainException {
		auth.untokenize(token);
		repository.setProperty(listener , GENERATED, "redis_consumer_listener" , redisPropkey );
		repository.setProperty(DAGNAME, GENERATED, dagname, listener);
		repository.setProperty(JARNAME, GENERATED, jarfile, listener);
	}
	@Override
	public void delListener(String token, String listener) throws DomainException {
		auth.untokenize(token);
		repository.delProperty(listener, redisPropkey);
		repository.delGroupProperty(listener);
	}
	private void convertToProperties(Properties inputProps, List<PropertyParameterDTO> propitem) {
		for (Iterator<PropertyParameterDTO> iterator = propitem.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			inputProps.put("value."+propertyParameterDTO.getName(), propertyParameterDTO.getValue());
			inputProps.put("desc."+propertyParameterDTO.getName(), propertyParameterDTO.getDescription());
			inputProps.put("group."+propertyParameterDTO.getName(), propertyParameterDTO.getGroup());
		}
	}
	@Override
	public void saveKafkaChannel(String token, String bootstrapServers, String groupId, Integer poll) throws DomainException {
		auth.untokenize(token);
		repository.setProperty("bootstrapServers", GENERATED, bootstrapServers, kafkaPropkey );
		repository.setProperty("groupId", GENERATED, groupId, kafkaPropkey );
		repository.setProperty("poll", GENERATED, poll.toString(), kafkaPropkey );
		repository.setProperty(STATUS, "kafka channel status", ACTIVE, kafkaPropkey );
	}
	@Override
	public void addConsumer(String token, String topic, String jarfile, String dagname) throws DomainException {
		auth.untokenize(token);
		repository.setProperty(topic , GENERATED, "kafka_consumer_listener" , kafkaPropkey );
		repository.setProperty(DAGNAME, GENERATED, dagname, topic);
		repository.setProperty(JARNAME, GENERATED, jarfile, topic);
		
	}
	@Override
	public void delConsumer(String token, String topic) throws DomainException {
		auth.untokenize(token);
		repository.delProperty(topic, redisPropkey);
		repository.delGroupProperty(topic);
	}
	@Override
	public void saveActiveMQChannel(String token, String host, String user, String pwd) throws DomainException {
		auth.untokenize(token);
		repository.setProperty("host", GENERATED, host, activeMQPropkey );
		repository.setProperty("user", GENERATED, user, activeMQPropkey );
		repository.setProperty("pwd", GENERATED, pwd, activeMQPropkey );
		repository.setProperty(STATUS, "activeMQ channel status", ACTIVE, activeMQPropkey );
	}
	
	@Override
	public void addConsumerAM(String token, String queue, String jarfile, String dagname) throws DomainException {
		auth.untokenize(token);
		repository.setProperty(queue , GENERATED, "activemq_consumer_listener" , activeMQPropkey );
		repository.setProperty(DAGNAME, GENERATED, dagname, queue);
		repository.setProperty(JARNAME, GENERATED, jarfile, queue);
	}
	@Override
	public void delConsumerAM(String token,String queue) throws DomainException {
		auth.untokenize(token);
		repository.delProperty(queue, activeMQPropkey);
		repository.delGroupProperty(queue);
	}
	@Override
	public void removeException(String token, String eventDt) throws DomainException {
		auth.untokenize(token);
		this.storage.removeException(eventDt);
	}
	@Override
	public void reimport(String token, String jarname) throws DomainException {
		auth.untokenize(token);
		JSONObject json = compiler.reimport(jarname);
		var list = repository.getUncompileds();
		for (Iterator<UncompiledDTO> iterator = list.iterator(); iterator.hasNext();) {
			UncompiledDTO uncompiledDTO = iterator.next();
			var dagjson = new JSONObject(uncompiledDTO.getBin());
			if(dagjson.get(JARNAME).equals(jarname)) {
				throw new DomainException(new Exception("design of jarname already exists"));
			}
		}
		repository.addUncompiled(json.getString(JARNAME),json);	
	}
	@Override
	public void logout(String token) throws DomainException {
		auth.logout(token);
		
	}
	@Override
	public void createFolder(String token, String foldername) throws DomainException {
		auth.untokenize(token);
		this.fileSystem.createFolder(foldername);
	}
	@Override
	public void deleteFile(String token, String folder, String file) throws DomainException {
		auth.untokenize(token);
		this.fileSystem.delete(folder,file);
	}
}
