package main.cl.dagserver.infra.adapters.operators;

import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={})
public class DummyOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug(this.getClass()+" end "+this.name);
		return null;
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.DummyOperator");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "dummy.png";
	}
	
}
