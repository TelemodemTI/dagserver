package example_dag.main;

import java.util.Properties;
import main.domain.core.DagExecutable;
import main.domain.enums.OperatorStatus;
import main.infra.adapters.operators.BatchOperator;
import main.infra.adapters.operators.DummyOperator;
import main.infra.adapters.operators.JdbcOperator;
import main.domain.annotations.Dag;

@Dag(name = "example_dag",cronExpr = "0 0/1 * * * ?", group="my_dags_group")
public class ExampleDag extends DagExecutable {

	public ExampleDag() throws Exception {
		super();
		
		var prop_reader_sql = new Properties();
		prop_reader_sql.setProperty("url", "jdbc:mysql://localhost:3306/sakila?useSSL=false");
		prop_reader_sql.setProperty("user", "root");
		prop_reader_sql.setProperty("pwd", "password");
		prop_reader_sql.setProperty("driver", "com.mysql.jdbc.Driver");
		prop_reader_sql.setProperty("query", "SELECT actor_id,first_name FROM actor");
		this.addOperator("sql_query",JdbcOperator.class, prop_reader_sql);
		
		Properties prop_updater_sql = (Properties) prop_reader_sql.clone();
		prop_updater_sql.setProperty("query", "INSERT INTO actor1 values(?)");
		prop_updater_sql.setProperty("xcom", "sql_query");
		this.addOperator("sql_insert",JdbcOperator.class, prop_updater_sql);
		this.addDependency("sql_query","sql_insert",OperatorStatus.OK);
		
		
	}
	
}
