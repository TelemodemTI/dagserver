package main.cl.dagserver.infra.adapters.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;


@Operator(args={"hostname","port","mode","redisCluster","keyObject"}, optionalv = {"xcom","body"})
public class RedisOperator extends OperatorStage implements Callable<List<Map<String, Object>>> {

	@Override
	public List<Map<String, Object>> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		List<Map<String, Object>> rv = new ArrayList<>();
		Boolean redisFlag = Boolean.parseBoolean(this.args.getProperty("redisCluster"));
		String mode = this.args.getProperty("mode");
		if(redisFlag) {
			Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
			String[] hostnames = this.args.getProperty("hostname").split(";");
			String[] ports = this.args.getProperty("port").split(";");
			for (int i = 0; i < hostnames.length; i++) {
				String string = hostnames[i];
				Integer portr = Integer.parseInt(ports[i]);
				jedisClusterNodes.add(new HostAndPort(string, portr));
			}
			JedisCluster jedisc = new JedisCluster(jedisClusterNodes);
			if(mode.equals("READ")) {
				rv = this.clusterRead(jedisc);
			} else if(mode.equals("SAVE")) {
				rv = this.clusterSave(jedisc);
			} else {
				rv = this.clusterDel(jedisc);
			}
		} else {
			JedisPool pool = new JedisPool(this.args.getProperty("hostname"), Integer.parseInt(this.args.getProperty("port")));
		    try (Jedis jedis = pool.getResource()) {
		    	if(mode.equals("READ")) {
					rv = this.singleRead(jedis);
				} else if(mode.equals("SAVE")) {
					rv = this.singleSave(jedis);
				} else {
					rv = this.singleDel(jedis);
				}	
		    } catch (Exception e) {
				throw new DomainException(e);
			}	
		}
		log.debug(this.getClass()+" end "+this.name);
		return rv;
	}
	
	private List<Map<String, Object>> clusterRead(JedisCluster jedisc){
		String data = jedisc.get(this.args.getProperty("keyObject"));
		List<Map<String, Object>> rv = new ArrayList<>();
		JSONArray arr = new JSONArray(data);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject row = arr.getJSONObject(i);
			Map<String, Object> map = row.toMap();
			rv.add(map);
		}
		return rv;
	}
	private List<Map<String, Object>> clusterSave(JedisCluster jedisc) throws DomainException{
		if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
			List<Map<String, Object>> rv = new ArrayList<>();  
			if(this.optionals.containsKey("xcom")) {
	    		  String xcomname = this.optionals.getProperty("xcom");
		    	  if(!this.xcom.has(xcomname)) {
						throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
		    	  }
		    	  var obj = (Object) this.xcom.get(xcomname);
		    	  jedisc.set(this.args.getProperty("keyObject"), obj.toString());	  
		  	   	  Map<String, Object> item = new HashMap<>();
		  	   	  item.put("status", "ok");
		  	   	  rv.add(item);  
	    	} else {
	    		  var body = this.optionals.getProperty("body");
	    		  jedisc.set(this.args.getProperty("keyObject"), body);
	    		  Map<String, Object> item = new HashMap<>();
		  	   	  item.put("status", "ok");
		  	   	  rv.add(item);  
	    	}
	  	   	return rv;
		} else { 
			throw new DomainException(new Exception("no xcom to save"));
		}
	}
	private List<Map<String, Object>> clusterDel(JedisCluster jedisc){
		jedisc.del(this.args.getProperty("keyObject"));
		List<Map<String, Object>> rv = new ArrayList<>();
	   	Map<String, Object> item = new HashMap<>();
	   	item.put("status", "ok");
	   	rv.add(item);
	   	return rv;
	}
	private List<Map<String, Object>> singleRead(Jedis jedis){
		String data = jedis.get(this.args.getProperty("keyObject"));
		List<Map<String, Object>> rv = new ArrayList<>();
		JSONArray arr = new JSONArray(data);
		for (int i = 0; i < arr.length(); i++) {
			JSONObject row = arr.getJSONObject(i);
			Map<String, Object> map = row.toMap();
			rv.add(map);
		}
		return rv;
	}
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> singleSave(Jedis jedis) throws DomainException{
		List<Map<String, Object>> rv = new ArrayList<>();
		if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.has(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
	    	  }
	    	  var obj = (List<Map<String, Object>>) this.xcom.get(xcomname);
	    	  jedis.set(this.args.getProperty("keyObject"), new JSONArray(obj).toString());
	    	  Map<String, Object> item = new HashMap<>();
	    	  item.put("status", "ok");
	    	  rv.add(item);
		} else {
			var body = this.optionals.getProperty("body");
  		    jedis.set(this.args.getProperty("keyObject"), body);
  		    Map<String, Object> item = new HashMap<>();
	  	   	item.put("status", "ok");
	  	   	rv.add(item);  
		}
		return rv;
	}
	private List<Map<String, Object>> singleDel(Jedis jedis){
		jedis.del(this.args.getProperty("keyObject"));
		List<Map<String, Object>> rv = new ArrayList<>();
   	  	Map<String, Object> item = new HashMap<>();
   	    item.put("status", "ok");
   	    rv.add(item);
	  	return rv;
	}

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.RedisOperator");
		metadata.setParameter("hostname", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("mode", "list", Arrays.asList("READ","SAVE","DELETE"));
		metadata.setParameter("redisCluster", "boolean");
		metadata.setParameter("keyObject", "text");
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts("body", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "redis.png";
	}
	
}
