package main.cl.dagserver.domain.dags;
import main.cl.dagserver.domain.core.DagExecutable;
import main.cl.dagserver.domain.enums.OperatorStatus;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.operators.LogsRollupOperator;
import main.cl.dagserver.infra.adapters.operators.RegisterSchedulerOperator;
import main.cl.dagserver.domain.annotations.Dag;


@Dag(name = "background_system_dag",cronExpr = "0 0/1 * ? * *", group="system_dags")
public class BackgroundSystemDag extends DagExecutable {

	public BackgroundSystemDag() throws DomainException {
		super();
		this.addOperator("internal",LogsRollupOperator.class);
		this.addOperator("register",RegisterSchedulerOperator.class);
		this.addDependency("internal", "register", OperatorStatus.ANY);
		
		
	}
	
}
