package main.cl.dagserver.domain.services;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.json.JSONObject;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import fr.brouillard.oss.security.xhub.XHub;
import fr.brouillard.oss.security.xhub.XHub.XHubConverter;
import fr.brouillard.oss.security.xhub.XHub.XHubDigest;
import main.cl.dagserver.application.ports.input.SchedulerMutationUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.enums.AccountType;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;

@Component
@ImportResource("classpath:properties-config.xml")
public class SchedulerMutationHandlerService extends BaseServiceComponent implements SchedulerMutationUseCase {

	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	private static final String JARNAME = "jarname";
	
	private static final SecureRandom random = new SecureRandom();
	
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
			var objauth = auth.untokenize(token);
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
			compiler.createJar(bin,force,prop,objauth);
				
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
	
	private void convertToProperties(Properties inputProps, List<PropertyParameterDTO> propitem) {
		for (Iterator<PropertyParameterDTO> iterator = propitem.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			inputProps.put("value."+propertyParameterDTO.getName(), propertyParameterDTO.getValue());
			inputProps.put("desc."+propertyParameterDTO.getName(), propertyParameterDTO.getDescription());
			inputProps.put("group."+propertyParameterDTO.getName(), propertyParameterDTO.getGroup());
		}
	}
	
	@Override
	public void removeException(String token, String eventDt) throws DomainException {
		auth.untokenize(token);
		this.storage.removeException(eventDt);
	}
	@Override
	public void reimport(String token, String jarname) throws DomainException {
		auth.untokenize(token);
		JSONObject reimportResult = compiler.reimport(jarname);
		
		// Verificar que no exista ya un diseño con el mismo jarname
		var list = repository.getUncompileds();
		for (Iterator<UncompiledDTO> iterator = list.iterator(); iterator.hasNext();) {
			UncompiledDTO uncompiledDTO = iterator.next();
			var dagjson = new JSONObject(uncompiledDTO.getBin());
			if(dagjson.get(JARNAME).equals(jarname)) {
				throw new DomainException(new Exception("design of jarname already exists"));
			}
		}
		
		// Extraer el dagdef.json y las propiedades
		JSONObject dagdef = reimportResult.getJSONObject("dagdef");
		JSONObject configProperties = reimportResult.getJSONObject("configProperties");
		
		// Guardar el diseño como uncompiled
		repository.addUncompiled(dagdef.getString(JARNAME), dagdef);
		
		// Procesar y cargar las propiedades a la base de datos
		if (configProperties != null && configProperties.length() > 0) {
			Properties props = new Properties();
			for (String key : configProperties.keySet()) {
				props.setProperty(key, configProperties.getString(key));
			}
			
			// Cargar las propiedades usando el método existente de QuartzConfig
			quartz.propertiesToRepo(props);
		}
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
	@Override
	public void copyFile(String token, String filename, String copyname) throws DomainException {
		auth.untokenize(token);
		this.fileSystem.copyFile(filename,copyname);
	}
	@Override
	public void moveFile(String token, String folder,String filename, String newpath) throws DomainException {
		auth.untokenize(token);
		this.fileSystem.moveFile(folder,filename,newpath);
	}
	@Override
	public void createApiKey(String token, String appname) throws DomainException {
		auth.untokenize(token);
		String newApikey = XHub.generateXHubToken(XHubConverter.HEXA_LOWERCASE, XHubDigest.SHA1, appname, this.generateRandomString(10).getBytes());
		this.repository.setProperty(appname, "API KEY for "+appname, newApikey, "HTTP_CHANNEL_API_KEY");
		
	}
	private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHABET.length());
            sb.append(ALPHABET.charAt(index));
        }
        return sb.toString();
    }
	@Override
	public void deleteApiKey(String token, String appname) throws DomainException {
		auth.untokenize(token);
		this.repository.delProperty(appname, "HTTP_CHANNEL_API_KEY");
	}
	@Override
	public void createKeyEntry(String token, String alias, String key, String pwd) throws DomainException {
		auth.untokenize(token);
		CredentialsDTO dto = new CredentialsDTO();
		dto.setPassword(pwd);
		dto.setUsername(key);
		this.keystore.createKey(alias,dto);
	}
	@Override
	public void removeEntry(String token, String alias) throws DomainException {
		auth.untokenize(token);
		this.keystore.removeKey(alias);
	}
}
