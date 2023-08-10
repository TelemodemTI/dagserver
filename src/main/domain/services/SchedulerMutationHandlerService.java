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
import main.domain.exceptions.DomainException;


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
	public void scheduleDag(String token, String dagname,String jarname) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			scanner.init().scheduler(dagname,jarname);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
		
	}
	@Override
	public void unscheduleDag(String token,String dagname,String jarname) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			scanner.init().unschedule(dagname,jarname);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void createProperty(String token, String name, String description, String value,String group) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.setProperty(name,description,value,group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteProperty(String token, String name,String group) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.delProperty(name,group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void execute(String token, String jarname, String dagname) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			scanner.init().execute(jarname, dagname);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void saveUncompiled(String token, JSONObject json) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.addUncompiled(json.getString("jarname"),json);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.updateUncompiled(uncompiled,json);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void compile(String token, Integer uncompiled, Boolean force) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			String bin = repository.getUncompiledBin(uncompiled);
			JSONObject def = new JSONObject(bin);
			String jarname = def.getString("jarname");
			compiler.createJar(bin,force);
			repository.createParams(jarname,bin);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteUncompiled(String token, Integer uncompiled) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.deleteUncompiled(uncompiled);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteGroupProperty(String token, String name, String group) throws DomainException {
		try {
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.delGroupProperty(group);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void createAccount(String token, String username, String accountType, String pwdHash) throws DomainException {
		try {
			var claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
			if(claimsmap.get("typeAccount").equals("ADMIN")) {
				repository.createAccount(username,accountType,pwdHash);
			} else {
				throw new DomainException("insufficient privileges");
			}	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	@Override
	public void deleteAccount(String token, String username) throws DomainException {
		try {
			var claims = TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			Map<String,String> claimsmap = (Map<String, String>) claims.get(CLAIMS);
			if(claimsmap.get("typeAccount").equals("ADMIN")) {
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
			TokenEngine.untokenize(token, jwt_secret, jwt_signer);
			repository.updateParams(idope, typeope, jarname, bin);	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	
}
