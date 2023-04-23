package main.application.ports.output;
import java.util.List;
import java.util.Map;
import main.domain.messages.DagDTO;
import main.infra.adapters.output.JarSchedulerAdapter;

public interface JarSchedulerOutputPort {

	public JarSchedulerAdapter init () throws Exception;
	
	Map<String, List<Map<String, String>>> getOperators();

	void scheduler(String dagname, String jarname) throws Exception;

	void unschedule(String dagname, String jarname) throws Exception;

	
	public List<DagDTO> getDagDetail(String jarname) throws Exception;

	public void execute(String jarname, String dagname) throws Exception;
	public List<Map<String,Object>> listScheduled() throws Exception;
}