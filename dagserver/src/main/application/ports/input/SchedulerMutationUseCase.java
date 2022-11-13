package main.application.ports.input;

public interface SchedulerMutationUseCase {

	void scheduleDag(String token, String dagname, String jarname) throws Exception;

	void unscheduleDag(String token, String dagname, String jarname) throws Exception;

}