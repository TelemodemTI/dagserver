package server.application.dags;

import java.util.Properties;
import server.application.core.DagExecutable;
import server.application.operators.OperatorStatus;
import server.application.operators.types.DummyOperator;
import server.infra.annotations.Dag;

@Dag(name = "event_system_dag", group="system_dags", onEnd="background_system_dag")
public class EventSystemDag extends DagExecutable {

	public EventSystemDag() throws Exception {
		super();

		this.addOperator("step1",DummyOperator.class, new Properties());
		this.addOperator("step2",DummyOperator.class, new Properties());
		
		this.addDependency("step1","step2",OperatorStatus.OK);

	}
	
}
