package main.domain.dags;

import java.util.Properties;
import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.infra.adapters.operators.BatchOperator;
import main.infra.adapters.operators.JdbcOperator;
import main.domain.annotations.Dag;

@Dag(name = "background_system_dag",cronExpr = "0 0/1 * * * ?", group="system_dags")
public class BackgroundSystemDag extends DagExecutable {

	public BackgroundSystemDag() throws Exception {
		super();
		var prop_sql = new Properties();
		prop_sql.setProperty("url", "jdbc:mysql://localhost:3306/sakila?useSSL=false");
		prop_sql.setProperty("user", "root");
		prop_sql.setProperty("pwd", "password");
		prop_sql.setProperty("driver", "com.mysql.jdbc.Driver");
		prop_sql.setProperty("query", "SELECT * FROM actor");
		this.addOperator("sql_query",JdbcOperator.class, prop_sql);
		var prop_batch = new Properties();
		prop_batch.setProperty("prefix", "cmd.exe");
		prop_batch.setProperty("c", "/c");
		prop_batch.setProperty("cmd", "java --version");
		this.addOperator("batch",BatchOperator.class, prop_batch);
		this.addDependency("sql_query","batch",OperatorStatus.OK);

	}
	
}
