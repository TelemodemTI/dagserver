package main.infra.adapters.input.graphql.types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import main.domain.exceptions.DomainException;
import main.infra.adapters.output.repositories.SchedulerRepository;
import net.bytebuddy.implementation.Implementation;

public class OperatorStage {
	protected static Logger log = Logger.getLogger("DAG");
	protected String name;

	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
    	return null;
    }
	
	public JSONObject getMetadataOperator() {
		return null;
	}
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

	public JSONObject getXcom() {
		return xcom;
	}

	public void setXcom(JSONObject xcom) {
		this.xcom = xcom;
	}
	
	protected JSONObject generateMetadata(JSONObject par,String canonicalname,Map<String,List<String>> opt) {
		JSONArray params = new JSONArray();
		for (String key : par.keySet()) {
            String value = par.getString(key);
            if(value.equals("list")) {
            	JSONArray optarr = new JSONArray();
            	var arrl = opt.get(key);
            	for (Iterator<String> iterator = arrl.iterator(); iterator.hasNext();) {
					String string = iterator.next();
					optarr.put(string);
				}
            	var obj = new JSONObject();
            	obj.put("name", key);
            	obj.put("type", value);
            	obj.put("opt", optarr);
            	params.put(obj);	
            } else {
            	var obj = new JSONObject();
            	obj.put("name", key);
            	obj.put("type", value);
            	params.put(obj);	
            }
        }
		String[] segments = canonicalname.split("\\.");
        String lastSegment = segments[segments.length - 1];
		
		JSONObject tag = new JSONObject();
		tag.put("class", canonicalname);
		tag.put("name", lastSegment);
		tag.put("params", params);
		return tag;
	}
	
	protected JSONObject generateMetadata(JSONObject par,String canonicalname) {
		Map<String,List<String>> opt = new HashMap<>();
		return this.generateMetadata(par, canonicalname,opt);
	}
}
