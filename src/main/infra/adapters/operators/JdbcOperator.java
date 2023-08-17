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
import org.json.JSONArray;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.BaseOperator;
import main.domain.exceptions.DomainException;



@Operator(args={"url","user","pwd","driver","query"},optionalv = { "xcom" })
public class JdbcOperator extends BaseOperator implements Callable<List<Map<String, Object>>> {
	
	private static final String QUERY = "query";
	
	@Override
	public List<Map<String, Object>> call() throws DomainException {		
		QueryRunner queryRunner = new QueryRunner();
		List<Map<String, Object>> result = new ArrayList<>();
		
		DbUtils.loadDriver(this.getClass().getClassLoader(), this.args.getProperty("driver"));
		String xcomname = this.args.getProperty("xcom");
		try(Connection con = DriverManager.getConnection(this.args.getProperty("url"), this.args.getProperty("user"), this.args.getProperty("pwd"));) {
			if(xcomname != null) {
				if(!this.xcom.has(xcomname)) {
					throw new DomainException("xcom not exist for dagname::"+xcomname);
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> data = (List<Map<String, Object>>) this.xcom.get(xcomname);	
				Object[][] objList = data.stream().map(m -> m.values().toArray()).toArray(Object[][]::new);
				
				if(this.args.getProperty(QUERY).split(" ")[0].equalsIgnoreCase("select")) {
					result = queryRunner.query(con, this.args.getProperty(QUERY), new MapListHandler(),data.get(0));	
				} else {
					queryRunner.batch(con,this.args.getProperty(QUERY), objList);
				}	
			} else {
					if(this.args.getProperty(QUERY).split(" ")[0].equalsIgnoreCase("select")) {
						result = queryRunner.query(con, this.args.getProperty(QUERY), new MapListHandler());	
					} else {
						queryRunner.update(con, this.args.getProperty(QUERY));
					}
			}	
		} catch (Exception e) {
			log.error(e);
		}
		return result;
	}
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"url\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"user\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"pwd\",type:\"password\"}"));
		params.put(new JSONObject("{name:\"driver\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"query\",type:\"sourcecode\"}"));
		
		JSONArray opts = new JSONArray();
		opts.put(new JSONObject("{name:\"xcom\",type:\"text\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.JdbcOperator");
		tag.put("name", "JdbcOperator");
		tag.put("params", params);
		tag.put("opt", opts);

		return tag;
	}
	@Override
	public String getIconImage() {
		return "jdbc.png";
	}
}
