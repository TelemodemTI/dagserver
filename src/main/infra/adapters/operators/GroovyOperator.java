package main.infra.adapters.operators;

import java.util.concurrent.Callable;
import org.json.JSONObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

@Operator(args={"source"})
public class GroovyOperator extends OperatorStage implements Callable<Object> {

	@Override
	public Object call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		String source = this.args.getProperty("source");
		
	    Binding binding = new Binding();
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
		return shell.evaluate(source);
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.GroovyOperator");
		metadata.setParameter("source", "sourcecode");
		return metadata.generate();
	}
	
	@Override
	public String getIconImage() {
		return "groovy.png";
	}
	
}
