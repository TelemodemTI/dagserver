package main.application.ports.output;
import java.util.List;
import java.util.Map;

import main.domain.exceptions.DomainException;
import main.domain.model.DagDTO;
import main.infra.adapters.output.scheduler.JarSchedulerAdapter;

public interface JarSchedulerOutputPort {

	public JarSchedulerAdapter init () throws DomainException;
	public Map<String, List<Map<String, String>>> getOperators();
	public void scheduler(String dagname, String jarname) throws DomainException;
	public void unschedule(String dagname, String jarname) throws DomainException;
	public List<DagDTO> getDagDetail(String jarname) throws DomainException;
	public void execute(String jarname, String dagname, String type) throws DomainException;
	public List<Map<String,Object>> listScheduled() throws DomainException;
	public String getIcons(String type) throws DomainException;
}