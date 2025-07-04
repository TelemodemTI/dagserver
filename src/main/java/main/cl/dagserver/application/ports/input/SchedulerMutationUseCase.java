package main.cl.dagserver.application.ports.input;

import org.json.JSONObject;

import main.cl.dagserver.domain.exceptions.DomainException;


public interface SchedulerMutationUseCase {

	void scheduleDag(String token, String dagname, String jarname) throws DomainException;
	void unscheduleDag(String token, String dagname, String jarname) throws DomainException;
	void createProperty(String token, String name, String description, String value, String group) throws DomainException;
	void deleteProperty(String token,String name, String group) throws DomainException;
	void execute(String token, String jarname, String dagname, String channel,String data) throws DomainException;
	void saveUncompiled(String token, JSONObject defobj) throws DomainException;
	void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws DomainException;
	void compile(String token, Integer uncompiled,Boolean force) throws DomainException;
	void deleteUncompiled(String token, Integer uncompiled) throws DomainException;
	void deleteGroupProperty(String token, String name, String group) throws DomainException;
	void createAccount(String token, String username, String accountType, String pwdHash) throws DomainException;
	void deleteAccount(String token, String username) throws DomainException;
	void updateParamsCompiled(String token, String idope, String typeope, String jarname, String bin) throws DomainException;
	void updateProp(String token, String group, String key, String value) throws DomainException;
	void deleteJarfile(String token, String jarname) throws DomainException;
	void deleteLog(String token, Integer logid) throws DomainException;
	void deleteAllLogs(String token, String dagname) throws DomainException;
	void renameUncompiled(String token, Integer uncompiled, String newname) throws DomainException;
	
	
	
	
	
	void removeException(String token, String eventDt) throws DomainException;
	void reimport(String token, String jarname) throws DomainException;
	void logout(String token) throws DomainException;
	void createFolder(String token, String foldername) throws DomainException;
	void deleteFile(String token, String folder, String file) throws DomainException;
	void copyFile(String token, String filename, String copyname) throws DomainException;
	void moveFile(String token, String folder,String filename, String newpath) throws DomainException;
	void createApiKey(String token, String appname) throws DomainException;
	void deleteApiKey(String token, String appname) throws DomainException;
	void createKeyEntry(String token, String alias, String key, String pwd) throws DomainException;
	void removeEntry(String token, String alias) throws DomainException;
	
}