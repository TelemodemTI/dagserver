package main.infra.adapters.input.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
	
	private Thread listener;
	private Map<String,Thread> bindings;
	
	@PostConstruct
	public void listener() {
		var root = this;
		this.bindings = new HashMap<String,Thread>();
		this.listener = new Thread(new Runnable() {
            public void run() {
            	while(true) {
            		try {
            			Properties redisprops = handler.getRedisChannelProperties();
                    	String status = redisprops.getProperty("STATUS");
            			if(status != null && status.equals("ACTIVE")){
            				Boolean mode = Boolean.parseBoolean(redisprops.getProperty("redisCluster"));
            				Properties listenersConfs = handler.getRedisListeners();
            				var publisher = new JedisPubSub() {
        	            	    @Override
        	            	    public void onMessage(String channel, String message) {
        	            	    	handler.raiseEvent(channel,message);
        	            	    }
        	            	};
            				if(mode) {	
            					for (String clave : listenersConfs.stringPropertyNames()) {
            			            if(!bindings.containsKey(clave)) {
            			            	log.debug("new redis channel detected::"+clave);
                    					
                    					Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
                    					List<KeyValue<String,Integer>> nodes = root.getConnectionInfoCluster(redisprops);
                        				for (Iterator<KeyValue<String, Integer>> iterator = nodes.iterator(); iterator.hasNext();) {
        									KeyValue<String, Integer> keyValue = iterator.next();
        									jedisClusterNodes.add(new HostAndPort(keyValue.getKey(), keyValue.getValue()));
        								}	
                        				Thread bindingThread = new Thread(new Runnable() {
                        					@Override
        									public void run() {
                        						try(JedisCluster jedisc = new JedisCluster(jedisClusterNodes);){
                            						jedisc.subscribe(publisher, clave);	
                            					} catch (Exception e) {
                            						log.error(e);
                								}			
                        					}
                        				});
                        				bindingThread.start();
                    					bindings.put(clave,bindingThread);
            			            }
            			        }	
                		} else {
            				for (String clave : listenersConfs.stringPropertyNames()) {
            					if(!bindings.containsKey(clave)) {
            						log.debug("new redis channel detected::"+clave);
            						var kv = root.getConectionInfo(redisprops);
                					Thread bindingThread = new Thread(new Runnable() {
    									@Override
    									public void run() {
    										try(Jedis jSubscriber = new Jedis(kv.getKey(),kv.getValue());){	
    		                	            	jSubscriber.subscribe(publisher, clave);
    		                            	} catch (Exception e) {
    		                            		log.error(e);
    		                				}	
    									}
                					});
                					bindingThread.start();
                					bindings.put(clave,bindingThread);	
            					}
            				}
            			}
            			Thread.sleep(redisRefresh);	
					}
            	} catch (Exception e) {
					log.error(e);
                	break;
				}
            }
           }
		});
		this.listener.start();
	}
	protected List<KeyValue<String, Integer>> getConnectionInfoCluster(Properties redisprops) {
		List<KeyValue<String, Integer>> newarr = new ArrayList<>();
		String[] hosts = redisprops.getProperty("hostname").split(";");
		String[] ports = redisprops.getProperty("port").split(";");
		for (int i = 0; i < hosts.length; i++) {
			String string = hosts[i];
			Integer portr = Integer.parseInt(ports[i]);
			KeyValue<String, Integer> rv = new KeyValue<>(string, portr);
			newarr.add(rv);
		}
		return newarr;
	}
	private KeyValue<String, Integer> getConectionInfo(Properties redisprops){
		Integer port = Integer.parseInt(redisprops.getProperty("port"));
		KeyValue<String, Integer> rv = new KeyValue<>(redisprops.getProperty("hostname"), port);
		return rv;
	}
}
