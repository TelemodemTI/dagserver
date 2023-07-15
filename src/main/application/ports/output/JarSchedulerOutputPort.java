package main.application.ports.output;
import java.util.List;
import java.util.Map;

import main.domain.model.DagDTO;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;

public interface JarSchedulerOutputPort {

	public JarSchedulerAdapter init () throws Exception;
	public Map<String, List<Map<String, String>>> getOperators();
	public void scheduler(String dagname, String jarname) throws Exception;
	public void unschedule(String dagname, String jarname) throws Exception;
	public List<DagDTO> getDagDetail(String jarname) throws Exception;
	public void execute(String jarname, String dagname) throws Exception;
	public List<Map<String,Object>> listScheduled() throws Exception;
	public String getIcons(String type) throws Exception;
}