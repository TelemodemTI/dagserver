package main.cl.dagserver.infra.adapters.operators;
import java.util.List;
import org.json.JSONObject;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.Dagmap;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;

@Operator(args={"source"})
public class GroovyOperator extends OperatorStage {

	@Override
	public List<Dagmap> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		String source = this.args.getProperty("source");
	    Binding binding = new Binding();
	    binding.setVariable("log", log);
	    binding.setVariable("xcom", xcom);
	    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader(), binding);
	    return Dagmap.createDagmaps(1, "output", shell.evaluate(source));
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
