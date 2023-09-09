package main.domain.dags;
import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.domain.exceptions.DomainException;
import main.infra.adapters.operators.LogsRollupOperator;
import main.infra.adapters.operators.RegisterSchedulerOperator;
import main.infra.adapters.operators.TestNGOperator;

import java.util.Properties;

import main.domain.annotations.Dag;


@Dag(name = "background_system_dag",cronExpr = "0 0 * * * ?", group="system_dags")
public class BackgroundSystemDag extends DagExecutable {

	public BackgroundSystemDag() throws DomainException {
		super();
		this.addOperator("internal",LogsRollupOperator.class);
		this.addOperator("register",RegisterSchedulerOperator.class);
		this.addDependency("internal", "register", OperatorStatus.ANY);
		
		
		var prop = new Properties();
		
		prop.setProperty("classpath", "C:\\tmp\\dagrags");
		prop.setProperty("reportOutput", "C:\\tmp\\report-output");
		this.addOperator("dummy",TestNGOperator.class,prop);
		this.addDependency("register", "dummy", OperatorStatus.ANY);
		
	}
	
}
