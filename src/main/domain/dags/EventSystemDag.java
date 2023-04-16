package main.domain.dags;

import main.domain.core.DagExecutable;
import main.infra.adapters.operators.DummyOperator;
import main.domain.annotations.Dag;

@Dag(name = "event_system_dag", group="system_dags", onEnd="background_system_dag")
public class EventSystemDag extends DagExecutable {

	public EventSystemDag() throws Exception {
		super();
		this.addOperator("Dummy",DummyOperator.class);
	}
	
}
