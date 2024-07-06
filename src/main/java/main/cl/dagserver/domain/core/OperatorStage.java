package main.cl.dagserver.domain.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;
import org.apache.log4j.Logger;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.repositories.SchedulerRepository;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
 
public abstract class OperatorStage implements Callable<DataFrame> {	
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;
	protected Properties args;
	protected Map<String,DataFrame> xcom;
	protected Properties optionals;
	
	public abstract DataFrame call() throws DomainException;
    public abstract JSONObject getMetadataOperator();
    
		
	public String getIconImage() {
		return "internal.png";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Properties getArgs() {
		return args;
	}
	public void setArgs(Properties args) {
		this.args = args;
	}
	public Properties getOptionals() {
		return optionals;
	}
	public void setOptionals(Properties optionals) {
		this.optionals = optionals;
	}
	protected SchedulerRepository getSchedulerRepository(ApplicationContext springContext) {
		SchedulerRepository repo = new SchedulerRepository();
		AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
		factory.autowireBean( repo );
		factory.initializeBean( repo, "schedulerRepository" );
		return repo;
	}
	protected JarSchedulerAdapter getScheduler(ApplicationContext springContext) {
		JarSchedulerAdapter adapter = new JarSchedulerAdapter();
		AutowireCapableBeanFactory factory = springContext.getAutowireCapableBeanFactory();
		factory.autowireBean( adapter );
		factory.initializeBean( adapter , "jarSchedulerAdapter" );
		return adapter;
	}
	public Map<String,DataFrame> getXcom() {
		return xcom;
	}
	public void setXcom(Map<String,DataFrame> xcom) {
		this.xcom = xcom;
	}
	public static DataFrame createStatusFrame(String status) {
		return DataFrame
		        .byArrayRow("status") 
		        .appender() 
		        .append(status)   
		        .toDataFrame();
    }
	public DataFrame createFrame(String key,Object value) {
		return DataFrame
		        .byArrayRow(key) 
		        .appender() 
		        .append(value)   
		        .toDataFrame();
	}
	

	@SuppressWarnings("removal")
	protected static DataFrame buildDataFrameFromMap(List<Map<String, Object>> list) {
        if (list == null || list.isEmpty()) {
            return DataFrame.newFrame("status").empty();
        } else {
        	Map<String, Object> firstRow = list.get(0);
            String[] columns = firstRow.keySet().toArray(new String[0]);
            var apender = DataFrame.byArrayRow(columns).appender();
            for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
    			Map<String, Object> map = iterator.next();
    			Object[] valuesArray = map.values().toArray(new Object[0]);
    			apender.append(valuesArray);
    		}
            return apender.toDataFrame();	
        }
         
    }
	@SuppressWarnings({ "removal", "unchecked" })
	protected static DataFrame buildDataFrameFromObject(List<Object> list) {
        if (list == null || list.isEmpty()) {
            return DataFrame.newFrame("status").empty();
        } else {
        	Object raw = list.get(0);
        	if(raw instanceof Map) {
        		var newl = list.stream().map(item -> (Map<String, Object>) item).collect(Collectors.toList());
        		return OperatorStage.buildDataFrameFromMap(newl);
        	} else {
        		Map<String, Object> firstRow = new HashMap<>();
            	firstRow.put("content", raw);
            	String[] columns = new String[1];
            	columns[0] = "content";
            	var apender = DataFrame.byArrayRow(columns).appender();		
            	for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
            			Object data =  iterator.next();
            			Map<String, Object> map = new HashMap<>();
            			map.put("content", data);
            			Object[] valuesArray = map.values().toArray(new Object[0]);
            			apender.append(valuesArray);
            	}
            	return apender.toDataFrame();
        	}
        }
         
    }
	
	
	public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class, String.class)).with(stepName, getClass(), propkey,optkey);
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
}
