package main.infra.adapters.operators;

import java.util.concurrent.Callable;

import main.domain.annotations.Operator;
import main.infra.adapters.input.graphql.types.OperatorStage;

@Operator(args={})
public class DummyOperator extends OperatorStage implements Callable<Void> {

	@Override
	public Void call() throws Exception {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		log.debug("xcom");
		log.debug(xcom);
		log.debug(this.getClass()+" end "+this.name);
		return null;
	}

}
