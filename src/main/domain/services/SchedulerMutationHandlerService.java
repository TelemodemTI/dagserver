package main.domain.services;

import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.input.SchedulerMutationUseCase;
import main.application.ports.output.CompilerOutputPort;
import main.application.ports.output.JarSchedulerOutputPort;
import main.application.ports.output.SchedulerRepositoryOutputPort;
import main.domain.core.TokenEngine;


@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerMutationHandlerService implements SchedulerMutationUseCase {
	
	private static final String CLAIMS = "claims";
	
	@Value( "${param.jwt_secret}" )
	private String jwt_secret;

	@Value( "${param.jwt_signer}" )
	private String jwt_signer;
	
	@Value( "${param.jwt_subject}" )
	private String jwt_subject;
	
	@Value( "${param.jwt_ttl}" )
	private Integer jwt_ttl;
	
	@Value( "${param.folderpath}" )
	private String path;
	
	@Autowired
	SchedulerRepositoryOutputPort repository;
	
	@Autowired 
	JarSchedulerOutputPort scanner;
	
	@Autowired
	CompilerOutputPort compiler;
		
	@SuppressWarnings("unused")
	private static Logger log = Logger.getLogger(SchedulerMutationHandlerService.class);
	
	
	
	@Override
	public void scheduleDag(String token, String dagname,String jarname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().scheduler(dagname,jarname);
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().unschedule(dagname,jarname);
	}
	@Override
	public void createProperty(String token, String name, String description, String value,String group) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.setProperty(name,description,value,group);
	}
	@Override
	public void deleteProperty(String token, String name,String group) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.delProperty(name,group);
	}
	@Override
	public void execute(String token, String jarname, String dagname) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		scanner.init().execute(jarname, dagname);
	}
	@Override
	public void saveUncompiled(String token, JSONObject json) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.addUncompiled(json.getString("jarname"),json);
	}
	@Override
	public void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.updateUncompiled(uncompiled,json);
	}
	@Override
	public void compile(String token, Integer uncompiled, Boolean force) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		String bin = repository.getUncompiledBin(uncompiled);
		JSONObject def = new JSONObject(bin);
		String jarname = def.getString("jarname");
		compiler.createJar(bin,force);
		repository.createParams(jarname,bin);
	}
	@Override
	public void deleteUncompiled(String token, Integer uncompiled) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.deleteUncompiled(uncompiled);
	}
	@Override
	public void deleteGroupProperty(String token, String name, String group) throws Exception {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.delGroupProperty(group);
	}
	@Override
	public void createAccount(String token, String username, String accountType, String pwdHash) throws Exception {
		var claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
		if(claimsmap.get("typeAccount").equals("ADMIN")) {
			repository.createAccount(username,accountType,pwdHash);
		} else {
			throw new Exception("insufficient privileges");
		}
	}
	@Override
	public void deleteAccount(String token, String username) throws Exception {
		var claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
		if(claimsmap.get("typeAccount").equals("ADMIN")) {
			repository.delAccount(username);
		} else {
			throw new Exception("insufficient privileges");
		}
		
	}
	@Override
	public void updateParamsCompiled(String token,String idope, String typeope, String jarname, String bin) throws Exception  {
		TokenEngine.untokenize(token, jwt_secret, jwt_signer);
		repository.updateParams(idope, typeope, jarname, bin);
	}
	
	
}
