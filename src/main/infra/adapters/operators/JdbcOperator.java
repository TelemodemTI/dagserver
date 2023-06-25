package main.infra.adapters.operators;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import main.domain.annotations.Operator;
import main.domain.core.DagExecutable;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;


@Operator(args={"url","user","pwd","driver","query"},optionalv = { "xcom" })
public class JdbcOperator extends OperatorStage implements Callable<List<Map<String, Object>>> {
	@Override
	public List<Map<String, Object>> call() throws Exception {		
		QueryRunner queryRunner = new QueryRunner();
		Connection con = DriverManager.getConnection(this.args.getProperty("url"), this.args.getProperty("user"), this.args.getProperty("pwd"));
		String xcomname = this.args.getProperty("xcom");
		List<Map<String, Object>> result = new ArrayList<>();
		try {
			DbUtils.loadDriver(this.args.getProperty("driver"));
			if(xcomname != null) {
				
				if(!this.xcom.has(xcomname)) {
					throw new Exception("xcom not exist for dagname::"+xcomname);
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> data = (List<Map<String, Object>>) this.xcom.get(xcomname);	
				Object[][] objList = data.stream().map(m -> m.values().toArray()).toArray(Object[][]::new);
				if(this.args.getProperty("query").split(" ")[0].toLowerCase().equals("select")) {
					result = queryRunner.query(con, this.args.getProperty("query"), new MapListHandler(),data.get(0));	
				} else {
					queryRunner.batch(con,this.args.getProperty("query"), objList);
				}	
			} else {
				if(this.args.getProperty("query").split(" ")[0].toLowerCase().equals("select")) {
					result = queryRunner.query(con, this.args.getProperty("query"), new MapListHandler());	
				} else {
					queryRunner.update(con, this.args.getProperty("query"));
				}
			}
	    } catch (Exception e) {
			throw e;
		} finally {
	      DbUtils.close(con);
	    }        
		return result;
	}
	@Override
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws Exception {
		Implementation implementation = MethodCall.invoke(DagExecutable.class.getConstructor())				
				.andThen(
						MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class , String.class)).with(stepName, JdbcOperator.class,propkey,optkey)
				);
		return implementation;
    }
}
