package main.application.ports.input;

import org.json.JSONObject;

import main.domain.exceptions.DomainException;

public interface SchedulerMutationUseCase {

	void scheduleDag(String token, String dagname, String jarname) throws DomainException;
	void unscheduleDag(String token, String dagname, String jarname) throws DomainException;
	void createProperty(String token, String name, String description, String value, String group) throws DomainException;
	void deleteProperty(String token,String name, String group) throws DomainException;
	public void execute(String token, String jarname, String dagname, String channel) throws DomainException;
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
	void addGitHubWebhook(String token, String repository, String secret, String secret2, String dagname, String jarname) throws DomainException;
	void removeGithubWebhook(String token, String name) throws DomainException;
	void deleteLog(String token, Integer logid) throws DomainException;
	void deleteAllLogs(String token, String dagname) throws DomainException;
	void renameUncompiled(String token, Integer uncompiled, String newname) throws DomainException;
	void saveRabbitChannel(String token, String host, String user, String pwd, Integer port) throws DomainException;
	void addQueue(String token, String queue, String jarfile, String dagname) throws DomainException;
	void delQueue(String token, String queue) throws DomainException;
	void saveRedisChannel(String token, String mode, String hostport, String channel, String jarfile, String dagname) throws DomainException;
}