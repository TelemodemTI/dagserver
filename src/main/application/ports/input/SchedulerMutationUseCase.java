package main.application.ports.input;

import org.json.JSONObject;

public interface SchedulerMutationUseCase {

	void scheduleDag(String token, String dagname, String jarname) throws Exception;
	void unscheduleDag(String token, String dagname, String jarname) throws Exception;
	void createProperty(String token, String name, String description, String value, String group) throws Exception;
	void deleteProperty(String token,String name, String group) throws Exception;
	public void execute(String token, String jarname, String dagname) throws Exception;
	void saveUncompiled(String token, JSONObject defobj) throws Exception;
	void updateUncompiled(String token,Integer uncompiled, JSONObject json) throws Exception;
	void compile(String token, Integer uncompiled,Boolean force) throws Exception;
	void deleteUncompiled(String token, Integer uncompiled) throws Exception;
}