package server.application.operators.types;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import server.application.operators.OperatorStage;
import server.infra.annotations.Operator;


@Operator(args={"url","user","pwd","driver","query"})
public class JdbcOperator extends OperatorStage implements Callable<List<Map<String, Object>>> {
	@Override
	public List<Map<String, Object>> call() throws Exception {		
		QueryRunner queryRunner = new QueryRunner();
		Connection con = DriverManager.getConnection(this.args.getProperty("url"), this.args.getProperty("user"), this.args.getProperty("pwd"));
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			DbUtils.loadDriver(this.args.getProperty("driver"));
			result = queryRunner.query(con, this.args.getProperty("query"), new MapListHandler());
	    } catch (Exception e) {
			throw e;
		} finally {
	      DbUtils.close(con);
	    }        
		return result;
	}
}
