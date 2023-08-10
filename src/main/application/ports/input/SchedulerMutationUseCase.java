package main.application.ports.input;

import org.json.JSONObject;

import main.domain.exceptions.DomainException;

public interface SchedulerMutationUseCase {

	void scheduleDag(String token, String dagname, String jarname) throws DomainException;
	void unscheduleDag(String token, String dagname, String jarname) throws DomainException;
	void createProperty(String token, String name, String description, String value, String group) throws DomainException;
	void deleteProperty(String token,String name, String group) throws DomainException;
	public void execute(String token, String jarname, String dagname) throws DomainException;
	void saveUncompiled(String token, JSONObject defobj) throws DomainException;
	void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws DomainException;
	void compile(String token, Integer uncompiled,Boolean force) throws DomainException;
	void deleteUncompiled(String token, Integer uncompiled) throws DomainException;
	void deleteGroupProperty(String token, String name, String group) throws DomainException;
	void createAccount(String token, String username, String accountType, String pwdHash) throws DomainException;
	void deleteAccount(String token, String username) throws DomainException;
	void updateParamsCompiled(String token, String idope, String typeope, String jarname, String bin) throws DomainException;
}