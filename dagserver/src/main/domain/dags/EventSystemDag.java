package main.domain.dags;

import java.util.Properties;
import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.infra.adapters.operators.DummyOperator;
import main.domain.annotations.Dag;

@Dag(name = "event_system_dag", group="system_dags", onEnd="background_system_dag")
public class EventSystemDag extends DagExecutable {

	public EventSystemDag() throws Exception {
		super();

		this.addOperator("step1",DummyOperator.class);
		this.addOperator("step2",DummyOperator.class);
		
		this.addDependency("step1","step2",OperatorStatus.OK);

	}
	
}
