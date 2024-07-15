package main.cl.dagserver.application.ports.output;

import java.util.Date;
import java.util.Map;

import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.core.ExceptionEventLog;

public interface StorageOutputPort {
	public void putEntry(String locatedb,Map<String,DataFrame> xcom);
	public Map<String,DataFrame> getEntry(String xcomkey);
	public void deleteXCOM(Date time);
	public void removeException(String eventDt);
	public void addException(ExceptionEventLog event);
	public Map<String, Object> listException(); 
}
