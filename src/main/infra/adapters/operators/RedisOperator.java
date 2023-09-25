package main.infra.adapters.operators;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;


@Operator(args={"hostname","port","mode","redisCluster","keyObject"}, optionalv = {"xcom"})
public class RedisOperator extends OperatorStage implements Callable<String> {

	@Override
	public String call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		StringBuilder rv = new StringBuilder();
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
				rv.append(this.clusterRead(jedisc));
			} else if(mode.equals("SAVE")) {
				rv.append(this.clusterSave(jedisc));
			} else {
				rv.append(this.clusterDel(jedisc));
			}
		} else {
			JedisPool pool = new JedisPool(this.args.getProperty("hostname"), Integer.parseInt(this.args.getProperty("port")));
		    try (Jedis jedis = pool.getResource()) {
		    	if(mode.equals("READ")) {
					rv.append(this.singleRead(jedis));
				} else if(mode.equals("SAVE")) {
					rv.append(this.singleSave(jedis));
				} else {
					rv.append(this.singleDel(jedis));
				}	
		    } catch (Exception e) {
				throw new DomainException(e.getMessage());
			}	
		}
		log.debug(this.getClass()+" end "+this.name);
		return rv.toString();
	}
	
	private String clusterRead(JedisCluster jedisc){
		return jedisc.get(this.args.getProperty("keyObject"));
	}
	private String clusterSave(JedisCluster jedisc) throws DomainException{
		if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.has(xcomname)) {
					throw new DomainException("xcom not exist for dagname::"+xcomname);
	    	  }
	    	  var obj = (Object) this.xcom.get(xcomname);
	    	  jedisc.set(this.args.getProperty("keyObject"), obj.toString());
	    	  JSONArray arr = new JSONArray();
	    	  JSONObject item = new JSONObject();
	    	  item.put("status", "ok");
	    	  arr.put(item);
	    	  return arr.toString();
		} else { 
			throw new DomainException("no xcom to save");
		}
	}
	private String clusterDel(JedisCluster jedisc){
		jedisc.del(this.args.getProperty("keyObject"));
		JSONArray arr = new JSONArray();
	  	JSONObject item = new JSONObject();
	  	item.put("status", "ok");
	  	arr.put(item);
	  	return arr.toString();
	}
	private String singleRead(Jedis jedis){
		return jedis.get(this.args.getProperty("keyObject"));
	}
	private String singleSave(Jedis jedis) throws DomainException{
		if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.has(xcomname)) {
					throw new DomainException("xcom not exist for dagname::"+xcomname);
	    	  }
	    	  var obj = (Object) this.xcom.get(xcomname);
	    	  jedis.set(this.args.getProperty("keyObject"), obj.toString());
	    	  JSONArray arr = new JSONArray();
	    	  JSONObject item = new JSONObject();
	    	  item.put("status", "ok");
	    	  arr.put(item);
	    	  return arr.toString();
		} else {
			throw new DomainException("no xcom to save");
		}
	}
	private String singleDel(Jedis jedis){
		jedis.del(this.args.getProperty("keyObject"));
		JSONArray arr = new JSONArray();
	  	JSONObject item = new JSONObject();
	  	item.put("status", "ok");
	  	arr.put(item);
	  	return arr.toString();
	}

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.RedisOperator");
		metadata.setParameter("hostname", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("mode", "mode", Arrays.asList("READ","SAVE","DELETE"));
		metadata.setParameter("redisCluster", "boolean");
		metadata.setParameter("keyObject", "text");
		metadata.setOpts("xcom", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "redis.png";
	}
	
}
