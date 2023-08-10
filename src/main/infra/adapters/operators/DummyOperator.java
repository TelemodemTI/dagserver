package main.infra.adapters.operators;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

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
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
		try {
			Implementation implementation = MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class)).with(stepName, DummyOperator.class);
			return implementation;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	@Override
	public JSONObject getMetadataOperator() {
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.DummyOperator");
		tag.put("name", "DummyOperator");
		tag.put("params", new JSONArray());
		return tag;
	}
	public String getIconImage() {
		return "dummy.png";
	}
	
}
