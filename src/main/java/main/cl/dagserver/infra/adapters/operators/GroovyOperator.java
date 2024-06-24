package main.cl.dagserver.infra.adapters.operators;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import joinery.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"source"})
public class GroovyOperator extends OperatorStage {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		String source = this.args.getProperty("source");
	    Binding binding = new Binding();
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
	    Object rv = shell.evaluate(source);
	    var df = new DataFrame();
	    if (rv instanceof List) {
	        var rvl = (List) rv;
	        df.add(rvl);
	    } else if (rv instanceof Map) {
	    	var rvm = (Map) rv;
	        df.add(Arrays.asList(rvm));
	    } else {
	        var newmap = new HashMap<String,Object>();
	        newmap.put("output", rv);
	        df.add(Arrays.asList(newmap));
	    }
	    return df;
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.GroovyOperator");
		metadata.setParameter("source", "sourcecode");
		return metadata.generate();
	}
	
	@Override
	public String getIconImage() {
		return "groovy.png";
	}
	
}
