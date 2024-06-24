package main.cl.dagserver.domain.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import joinery.DataFrame;

import org.apache.log4j.Logger;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.output.repositories.SchedulerRepository;
import main.cl.dagserver.infra.adapters.output.scheduler.JarSchedulerAdapter;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;
 

@SuppressWarnings("rawtypes")
public abstract class OperatorStage implements Callable<DataFrame> {	
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;

	
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

	protected Properties args;
	protected JSONObject xcom = new JSONObject();

	
	public Properties getArgs() {
		return args;
	}

	public void setArgs(Properties args) {
		this.args = args;
	}

	
	protected Properties optionals;
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
	
	
	public JSONObject getXcom() {
		return xcom;
	}

	public void setXcom(JSONObject xcom) {
		this.xcom = xcom;
	}
	
	
	@SuppressWarnings("unchecked")
	public DataFrame createStatusFrame(String status) {
		DataFrame df = new DataFrame();
		Map<String,String> rmap = new HashMap<>();
		rmap.put("status", status);
		df.add(Arrays.asList(rmap));
		return df;
	}
	
	@SuppressWarnings("unchecked")
	public DataFrame createFrame(String key,Object value) {
		DataFrame df = new DataFrame();
		Map<String,Object> rmap = new HashMap<>();
		rmap.put(key, value);
		df.add(Arrays.asList(rmap));
		return df;
	}
	
	protected JSONArray dataFrameToJson(DataFrame<Object> dataFrame) {
	        JSONArray jsonArray = new JSONArray();
	        for (List<Object> row : dataFrame) {
	            JSONObject jsonObject = new JSONObject();
	            List<Object> columns = new ArrayList<>(dataFrame.columns());
	            for (int i = 0; i < columns.size(); i++) {
	                String columnName = columns.get(i).toString();
	                Object cellValue = row.get(i);
	                jsonObject.put(columnName, cellValue);
	            }
	            jsonArray.put(jsonObject);
	        }
	        return jsonArray;
	}
	protected DataFrame buildDataFrame(List<Map<String,Object>> list) {
		DataFrame<Object> dataFrame = new DataFrame<>();
        if (!list.isEmpty()) {
            // Assuming all maps have the same keys
            Map<String, Object> firstRow = list.get(0);
            List<String> columns = new ArrayList<>(firstRow.keySet());
            dataFrame.add(columns.toArray());

            // Add each map as a row in the DataFrame
            for (Map<String, Object> map : list) {
                List<Object> row = new ArrayList<>();
                for (String column : columns) {
                    row.add(map.get(column));
                }
                dataFrame.append(row);
            }
        }
        return dataFrame;
	}
	public Implementation getDinamicInvoke(String stepName, String propkey, String optkey) throws DomainException {
        try {
            return MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class, String.class)).with(stepName, getClass(), propkey,optkey);
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
}
