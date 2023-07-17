package main.infra.adapters.operators;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"source"})
public class GroovyOperator extends OperatorStage implements Callable<Object> {

	@Override
	public Object call() throws Exception {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		String source = this.args.getProperty("source");
		
	    Binding binding = new Binding();
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    GroovyShell shell = new GroovyShell(binding);
		
	    
	    
		return shell.evaluate(source);
	}
	
	private Map<String, Object> convertJSONObjectToMap(JSONObject jsonObject) {
	    Map<String, Object> map = new HashMap<>();
	    for (String key : jsonObject.keySet()) {
	        Object value = jsonObject.get(key);
	        map.put(key, value);
	    }
	    return map;
	}
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws Exception {
		Implementation implementation = MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class )).with(stepName, GroovyOperator.class,propkey);
		return implementation;
	}

	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"source\",type:\"sourcecode\"}"));
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.GroovyOperator");
		tag.put("name", "GroovyOperator");
		tag.put("params", params);
		return tag;
	}
	public String getIconImage() {
		return "groovy.png";
	}
	
}
