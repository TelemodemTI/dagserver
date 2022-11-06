package example_dag.main;

import java.util.Properties;
import server.application.core.DagExecutable;
import server.application.operators.OperatorStatus;
import server.application.operators.types.DummyOperator;
import server.infra.annotations.Dag;

@Dag(name = "example_dag",cronExpr = "0 0/5 * * * ?", group="my_dags_group")
public class ExampleDag extends DagExecutable {

	public ExampleDag() throws Exception {
		super();
		this.addOperator("step1",DummyOperator.class, new Properties());
		this.addOperator("step2",DummyOperator.class, new Properties());
		this.addDependency("step1","step2",OperatorStatus.OK);
		
	}
	
}
