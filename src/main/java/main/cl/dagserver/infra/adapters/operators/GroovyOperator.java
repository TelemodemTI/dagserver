package main.cl.dagserver.infra.adapters.operators;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import com.nhl.dflib.DataFrame;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DagOperatorApi;
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
	    var dagapi = new DagOperatorApi();
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    binding.setVariable("operator", dagapi);
	    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
	    Object rv = shell.evaluate(source);
	    
	    if (rv instanceof List) {
	        var rvl = (List) rv;
	        if(rvl.isEmpty()) {
	        	return DataFrame.empty("status");
	        } else {
	        	return OperatorStage.buildDataFrame(rvl);	
	        }
	    } else if (rv instanceof Map) {
	    	var rvm = (Map) rv;
	    	if(rvm.isEmpty()) {
	    		return DataFrame.empty("status");
	    	} else {
	    		return OperatorStage.buildDataFrame(Arrays.asList(rvm));	
	    	}
	    } else if(rv instanceof DataFrame) {
	    	return (DataFrame) rv;
	    } else if(rv == null) {
	    	return DataFrame.empty("status");
	    } else {
	        return this.createFrame("output", rv);
	    }
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
