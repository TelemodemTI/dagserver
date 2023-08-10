package example_dag.main;

import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.domain.exceptions.DomainException;
import main.infra.adapters.operators.DummyOperator;
import main.domain.annotations.Dag;

@Dag(name = "custom_dag_example",cronExpr = "0 0/1 * * * ?", group="my_dags_group")
public class CustomDagExample extends DagExecutable {

	public CustomDagExample() throws DomainException {
		super();
		this.addOperator("Dummy",DummyOperator.class);
		this.addOperator("Dummy2",DummyOperator.class);
		this.addOperator("Dummy3",DummyOperator.class);
		this.addOperator("Dummy4",DummyOperator.class);
		
		this.addDependency("Dummy", "Dummy3", OperatorStatus.OK );
		this.addDependency("Dummy3", "Dummy4", OperatorStatus.OK );
	    this.addDependency("Dummy4", "Dummy2", OperatorStatus.OK );
	}

}