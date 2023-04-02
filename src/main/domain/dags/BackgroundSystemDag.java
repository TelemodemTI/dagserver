package main.domain.dags;
import main.domain.core.DagExecutable;
import main.infra.adapters.operators.LogsRollupOperator;
import main.domain.annotations.Dag;


@Dag(name = "background_system_dag",cronExpr = "0 0/60 * * * ?", group="system_dags")
public class BackgroundSystemDag extends DagExecutable {

	public BackgroundSystemDag() throws Exception {
		super();
		this.addOperator("internal",LogsRollupOperator.class);
	}
	
}
