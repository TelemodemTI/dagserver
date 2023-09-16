package main.infra.adapters.operators;

import java.io.File;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.KeyValue;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import main.infra.adapters.confs.DagPathClassLoadHelper;



@Operator(args={"url","user","pwd","driver","driverPath","query"},optionalv = { "xcom" })
public class JdbcOperator extends OperatorStage implements Callable<List<Map<String, Object>>> {
	
	private DagPathClassLoadHelper helper = new DagPathClassLoadHelper();
	private static final String QUERY = "query";
	
	@Override
	public List<Map<String, Object>> call() throws DomainException {		
		QueryRunner queryRunner = new QueryRunner();
		List<Map<String, Object>> result = new ArrayList<>();
		List<String> archivosJar = new ArrayList<>();
		this.searchJarFiles(new File(this.args.getProperty("driverPath")),archivosJar);
		List<URI> list = new ArrayList<>();
		for (Iterator<String> iterator = archivosJar.iterator(); iterator.hasNext();) {
			String jarpath = iterator.next();
			list.add(new File(jarpath).toURI());
		}
		DbUtils.loadDriver(helper.getClassLoader(list), this.args.getProperty("driver"));
		String xcomname = this.optionals.getProperty("xcom");
		try(Connection con = DriverManager.getConnection(this.args.getProperty("url"), this.args.getProperty("user"), this.args.getProperty("pwd"));) {
			if(xcomname != null && !xcomname.isEmpty()) {
				if(!this.xcom.has(xcomname)) {
					throw new DomainException("xcom not exist for dagname::"+xcomname);
				}
				@SuppressWarnings("unchecked")
				List<Map<String, Object>> data = (List<Map<String, Object>>) this.xcom.get(xcomname);	
				if(this.args.getProperty(QUERY).split(" ")[0].equalsIgnoreCase("select")) {
					String sql = this.args.getProperty(QUERY);
					var map = data.get(0);
					var kv = this.namedParameter(sql, map);
					result = queryRunner.query(con, kv.getKey(), new MapListHandler(),kv.getValue());
				} else {
					String sql = this.args.getProperty(QUERY);
					for (Iterator<Map<String, Object>> iterator = data.iterator(); iterator.hasNext();) {
						Map<String, Object> map =  iterator.next();
					    var kv = this.namedParameter(sql, map);
					    queryRunner.update(con, kv.getKey(), kv.getValue());
					}
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
	private KeyValue<String, Object[]> namedParameter(String sql,Map<String, Object> map) {
		Pattern pattern = Pattern.compile(":\\w+");
	    Matcher matcher = pattern.matcher(sql);
	    List<String> paramNames = new ArrayList<>();
	    while (matcher.find()) {
	        String paramName = matcher.group().substring(1); 
	        paramNames.add(paramName);
	    }
	    Object[] objList = new Object[paramNames.size()];
	    String sqlWithPlaceholders = sql.replaceAll(":\\w+", "?");
	    for (int i = 0; i < paramNames.size(); i++) {
	        String paramName = paramNames.get(i);
	        objList[i] = map.get(paramName);
	    }
	    KeyValue<String, Object[]> keyValue = new KeyValue<>(sqlWithPlaceholders, objList);
	    return keyValue;
	}
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.JdbcOperator");
		metadata.setParameter("url", "text");
		metadata.setParameter("user", "text");
		metadata.setParameter("pwd", "password");
		metadata.setParameter("driver", "text");
		metadata.setParameter("driverPath", "text");
		metadata.setParameter(QUERY, "sourcecode");
		metadata.setOpts("xcom","text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "jdbc.png";
	}
	private void searchJarFiles(File directorio, List<String> archivosJar) {
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (archivo.isFile() && archivo.getName().endsWith(".jar")) {
                    archivosJar.add(archivo.getAbsolutePath());
                } else if (archivo.isDirectory()) {
                	searchJarFiles(archivo, archivosJar);
                }
            }
        }
    }

}
