package main.cl.dagserver.infra.adapters.operators;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import main.cl.dagserver.application.ports.output.JarSchedulerOutputPort;
import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DagGraphApi;
import main.cl.dagserver.domain.core.DagOperatorApi;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

@Operator(args={"source"})
public class GroovyOperator extends OperatorStage {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		ApplicationContext appCtx = ApplicationContextUtils.getApplicationContext();
		var repo =  appCtx.getBean("schedulerRepository", SchedulerRepositoryOutputPort.class);
		var scanner =  appCtx.getBean("jarSchedulerAdapter", JarSchedulerOutputPort.class);
		
		String source = this.getInputProperty("source");

	    Binding binding = new Binding();
	    var dagapi = new DagOperatorApi();
	    var dagdag = new DagGraphApi(repo,scanner);
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    binding.setVariable("operator", dagapi);
	    binding.setVariable("dag", dagdag);
	    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
	    Object rv = shell.evaluate(source);
	    
	    log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
	    
	    if (rv instanceof List) {
	        var rvl = (List) rv;
	        if(rvl.isEmpty()) {
	        	return DataFrame.empty("status");
	        } else {
	        	return DataFrameUtils.buildDataFrameFromObject(rvl);	
	        }
	    } else if (rv instanceof Map) {
	    	var rvm = (Map) rv;
	    	if(rvm.isEmpty()) {
	    		return DataFrame.empty("status");
	    	} else {
	    		return DataFrameUtils.buildDataFrameFromMap(Arrays.asList(rvm));	
	    	}
	    } else if(rv instanceof DataFrame) {
	    	return (DataFrame) rv;
	    } else if(rv == null) {
	    	return DataFrame.empty("status");
	    } else {
	        return DataFrameUtils.createFrame("output", rv);
	    }
	    
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.GroovyOperator");
		metadata.setType("PROCCESS");
		metadata.setParameter("source", "sourcecode", Arrays.asList("text/x-groovy"));
		return metadata.generate();
	}
	
	@Override
	public String getIconImage() {
		return "groovy.png";
	}
	
}
