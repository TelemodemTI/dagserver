package main.cl.dagserver.application.ports.output;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.DagDTO;

public interface JarSchedulerOutputPort {

	public JarSchedulerOutputPort init () throws DomainException;
	public Map<String, List<Map<String, String>>> getOperators();
	public void scheduler(String dagname, String jarname) throws DomainException;
	public void unschedule(String dagname, String jarname) throws DomainException;
	public List<DagDTO> getDagDetail(String jarname) throws DomainException;
	public CompletableFuture<Map<String, DataFrame>> execute(String jarname, String dagname, String type, String data) throws DomainException;
	public List<Map<String,Object>> listScheduled() throws DomainException;
	public String getIcons(String type) throws DomainException;
	public void deleteXCOM(Date time)  throws DomainException;
	public boolean isEnabled(String activemqListener) throws DomainException;
}