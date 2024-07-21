package main.cl.dagserver.domain.dags;

import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.operators.DummyOperator;
import main.cl.dagserver.domain.annotations.Dag;


@Dag(name = "event_system_dag", group="system_dags", onEnd="background_system_dag", target = "DAG")
public class EventSystemDag extends DagExecutable {

	public EventSystemDag() throws DomainException {
		super();
		this.addOperator("dummy",DummyOperator.class);
	}
}
