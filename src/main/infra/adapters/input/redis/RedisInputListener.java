package main.infra.adapters.input.redis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.application.ports.input.RedisChannelUseCase;
import main.domain.core.KeyValue;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;


@Component
@ImportResource("classpath:properties-config.xml")
public class RedisInputListener {

	private static Logger log = Logger.getLogger(RedisInputListener.class);
	
	@Autowired
	RedisChannelUseCase handler;
	
	@Value( "${param.redis.refresh.timeout}" )
	private Integer redisRefresh;
	
	@PostConstruct
	public void listener() {
		var root = this;
		new Thread(new Runnable() {
            public void run() {
            	while(true) {
            		try {
            			Properties redisprops = handler.getRedisChannelProperties();
                    	String status = redisprops.getProperty("STATUS");
            			if(status != null && status.equals("ACTIVE")){
            				Boolean mode = Boolean.parseBoolean(redisprops.getProperty("redisCluster"));
            				var publisher = new JedisPubSub() {
        	            	    @Override
        	            	    public void onMessage(String channel, String message) {
        	            	    	handler.raiseEvent(channel,message);
        	            	    }
        	            	};
            				if(mode) {
            					Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
            					List<KeyValue<String,Integer>> nodes = root.getConnectionInfoCluster(redisprops);
                				for (Iterator<KeyValue<String, Integer>> iterator = nodes.iterator(); iterator.hasNext();) {
									KeyValue<String, Integer> keyValue = iterator.next();
									jedisClusterNodes.add(new HostAndPort(keyValue.getKey(), keyValue.getValue()));
								}
            					try(JedisCluster jedisc = new JedisCluster(jedisClusterNodes);){
            						jedisc.subscribe(publisher, redisprops.getProperty("channel"));	
            					} catch (Exception e) {
            						log.error(e);
                            		break;
								}
            				} else {
            					var kv = root.getConectionInfo(redisprops);
            					try(Jedis jSubscriber = new Jedis(kv.getKey(),kv.getValue());){	
                	            	jSubscriber.subscribe(publisher, redisprops.getProperty("channel"));
                            	} catch (Exception e) {
                            		
                				}	
            				}
            			}
            			Thread.sleep(redisRefresh);	
					} catch (Exception e) {
						log.error(e);
                		break;
					}
            	}
            }
		});
	}
	protected List<KeyValue<String, Integer>> getConnectionInfoCluster(Properties redisprops) {
		// TODO Auto-generated method stub
		return null;
	}
	private KeyValue<String, Integer> getConectionInfo(Properties redisprops){
		return null;
	}
}
