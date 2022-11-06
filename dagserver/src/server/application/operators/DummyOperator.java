package server.application.operators;

import java.util.concurrent.Callable;

import server.infra.annotations.Operator;

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
