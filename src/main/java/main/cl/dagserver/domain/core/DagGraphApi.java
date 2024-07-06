package main.cl.dagserver.domain.core;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.exceptions.DomainException;

public class DagGraphApi {
	private Boolean compiled;
	private Properties prop;
	private Properties opts;
	private SchedulerRepositoryOutputPort repository;
	private JarSchedulerOutputPort scanner;
	private static final String PARAMS = "params";
	private static final String VALUE = "value";
	
	public DagGraphApi(SchedulerRepositoryOutputPort repository, JarSchedulerOutputPort scanner) {
		this.repository=repository;
		this.scanner = scanner;
	}
	
	public void execute(String jarname,String dagname) throws DomainException {
		this.execute(jarname, dagname,null);
	}
	public void execute(String jarname,String dagname, String data) throws DomainException {
		if(compiled != null && prop != null && opts != null) {
			if(compiled) {
				this.executeCompiledDag(jarname,dagname,data);
			} else {
				this.executeUncompiledDag(dagname);
			}	
		} else {
			throw new DomainException(new Exception("invalid dag to execute"));
		}
	}
	private void executeUncompiledDag(String dagname) throws DomainException {
		String json = repository.getUncompiledBinByName(dagname);
		JSONObject daguncompiled = new JSONObject(json);
		JSONArray dags = daguncompiled.getJSONArray("dags");
		TemporalDagExecutable dagtmp = new TemporalDagExecutable();
		for (int i = 0; i < dags.length(); i++) {
			JSONObject dagobj = dags.getJSONObject(i);
			if(dagobj.getString("name").equals(dagname)) {
				JSONArray steps = dagobj.getJSONArray("boxes");
				try {
					setupDAG(steps,dagtmp);
				} catch (Exception e) {
					throw new DomainException(e);
				}	
			}
		}
	}
	private void executeCompiledDag(String jarname,String dagname,String data) throws DomainException {
			this.scanner.execute(jarname, dagname, dagname,data);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DagGraphApi setArgs(Map propmap,Map optsmap) {
		this.setArgs(mapToProperties(propmap), mapToProperties(optsmap));
		return this;
	}
	
	public DagGraphApi setArgs(String propstr,String optsstr) {
		JSONObject propjson = new JSONObject(propstr);
		JSONObject optsjson = new JSONObject(optsstr);
		return this.setArgs(this.jsonToProperties(propjson), this.jsonToProperties(optsjson));
	}
	public DagGraphApi setArgs(Properties prop,Properties opts) {
		this.prop = prop;
		this.opts = opts;
		return this;
	}
	public DagGraphApi isCompiled(Boolean type) {
		this.compiled = type;
		return this;
	}
	private Properties jsonToProperties(JSONObject jsonObject) {
        Properties properties = new Properties();
        Iterator<String> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonObject.getString(key);
            properties.put(key, value);
        }
        return properties;
    }
	private void setupDAG(JSONArray steps,TemporalDagExecutable dagtmp) throws ClassNotFoundException, JSONException, DomainException {
		for (int j = 0; j < steps.length(); j++) {
			JSONObject step = steps.getJSONObject(j);
			Class<?> impl = Class.forName(step.getString("type"));
		    Operator operatorAnnotation = impl.getAnnotation(Operator.class);
		    Properties properties = null;
		    Properties options = new Properties();
		    if (operatorAnnotation != null) {
		            properties = loadProperties(operatorAnnotation.args(),step);
		            options = loadOptionals(operatorAnnotation.optionalv(), step);
		    }
			dagtmp.addOperator(step.getString("id"), impl, properties , options);	
			
		}
		for (int j = 0; j < steps.length(); j++) {
			JSONObject step = steps.getJSONObject(j);		
			if(step.has("source")) {
				String sourceStepname = step.getJSONObject("source").getJSONObject("attrs").getJSONObject("label").getString("text");
				String status = step.getString("status").isEmpty() ? "ANY":step.getString("status");
				dagtmp.addDependency(sourceStepname, step.getString("id"),status );
			}
		}
	}
	private Properties loadProperties(String[] args,JSONObject step) {
		Properties properties = new Properties();
		for (String arg : args) {
            if (step.has(PARAMS)) {
                JSONArray params = step.getJSONArray(PARAMS);
                for (int k = 0; k < params.length(); k++) {
                    JSONObject param = params.getJSONObject(k);
                    if (param.has("key") && param.has(VALUE) && param.getString("key").equals(arg)) {
                        properties.setProperty(arg, param.getString(VALUE));
                    }
                }
            }
        }
		return properties;
	}
	private Properties loadOptionals(String[] optionals,JSONObject step) {
		Properties options = new Properties();
		for (String arg : optionals) {
            if (step.has(PARAMS)) {
                JSONArray params = step.getJSONArray(PARAMS);
                for (int k = 0; k < params.length(); k++) {
                    JSONObject param = params.getJSONObject(k);
                    if (param.has("key") && param.has(VALUE) && !param.isNull(VALUE) && param.getString("key").equals(arg) && !param.getString(VALUE).isEmpty()) {
                       options.setProperty(arg, param.getString(VALUE));
                    }	
                }
            }
        }
		return options;
	}
	public Properties mapToProperties(Map<String, Object> map) {
        Properties properties = new Properties();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            properties.put(entry.getKey(), entry.getValue().toString());
        }
        return properties;
    }
}
