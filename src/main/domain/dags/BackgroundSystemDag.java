package main.domain.dags;
import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.infra.adapters.operators.LogsRollupOperator;
import main.infra.adapters.operators.RegisterSchedulerOperator;
import main.domain.annotations.Dag;


@Dag(name = "background_system_dag",cronExpr = "0 0/60 * * * ?", group="system_dags")
public class BackgroundSystemDag extends DagExecutable {

	public BackgroundSystemDag() throws Exception {
		super();
		this.addOperator("internal",LogsRollupOperator.class);
		this.addOperator("register",RegisterSchedulerOperator.class);
		this.addDependency("internal", "register", OperatorStatus.ANY);
	}
	
}
