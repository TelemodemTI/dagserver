package main.cl.dagserver.infra.adapters.input.redis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.core.KeyValue;
import main.cl.dagserver.domain.exceptions.DomainException;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;


@Component
@ImportResource("classpath:properties-config.xml")
public class RedisInputListener {
	
	
	private RedisChannelUseCase handler;
    private ApplicationEventPublisher eventPublisher;
	
	@Value( "${param.redis.refresh.timeout}" )
	private Integer redisRefresh;
	
	
	private Map<String,Thread> bindings;
	private static final String LISTENER = "listener";
	private Boolean someCondition = false;
	
	@Autowired
	public RedisInputListener(RedisChannelUseCase handler,ApplicationEventPublisher eventPublisher) {
		this.handler = handler;
		this.eventPublisher = eventPublisher;
	}
	
	@PostConstruct
	public void listener() {
		this.bindings = new HashMap<>();
		 Thread listenerT = new Thread(() -> {
            
            	boolean keepRunning = true;
            	while(keepRunning) {
            		try {
            			Properties redisprops = handler.getRedisChannelProperties();
                    	String status = redisprops.getProperty("STATUS");
            			if(status != null && status.equals("ACTIVE")){
            				listenerActive(redisprops);
            			}
            			if (someCondition.equals(Boolean.TRUE)) {
                             keepRunning = false;
                        }
            			Thread.sleep(redisRefresh);	
            	} catch (InterruptedException ie) {
            		Thread.currentThread().interrupt();
            	} catch (Exception e) {
            		eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
                	break;
				}
            }
		});
		listenerT.start();
	}
	
	private void listenerActive(Properties redisprops) throws DomainException {
		Boolean mode = Boolean.parseBoolean(redisprops.getProperty("redisCluster"));
		Properties listenersConfs = handler.getRedisListeners();
		var publisher = getPublisher();
		if(mode.equals(Boolean.TRUE)) {	
			listenerCluster(listenersConfs,redisprops,publisher);
		} else {
			for (String clave : listenersConfs.stringPropertyNames()) {
				if(!bindings.containsKey(clave)) {
					var kv = this.getConectionInfo(redisprops);
					Thread bindingThread = getThreadAlt(publisher,clave,kv);
					bindingThread.start();
					bindings.put(clave,bindingThread);	
				}
			}
		}
	}
	
	private void listenerCluster(Properties listenersConfs,Properties redisprops,JedisPubSub publisher) {
		for (String clave : listenersConfs.stringPropertyNames()) {
            if(!bindings.containsKey(clave)) {
				Set<HostAndPort> jedisClusterNodes = new HashSet<>();
				List<KeyValue<String,Integer>> nodes = this.getConnectionInfoCluster(redisprops);
				for (Iterator<KeyValue<String, Integer>> iterator = nodes.iterator(); iterator.hasNext();) {
					KeyValue<String, Integer> keyValue = iterator.next();
					jedisClusterNodes.add(new HostAndPort(keyValue.getKey(), keyValue.getValue()));
				}	
				Thread bindingThread = getThread(publisher,jedisClusterNodes,clave);
				bindingThread.start();
				bindings.put(clave,bindingThread);
            }
        }	
	}
	private Thread getThreadAlt(JedisPubSub publisher, String clave, KeyValue<String, Integer> kv) {
	    return new Thread(() -> {
	        try (Jedis jSubscriber = new Jedis(kv.getKey(), kv.getValue())) {
	            jSubscriber.subscribe(publisher, clave);
	        } catch (Exception e) {
	            eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
	        }
	    });
	}
	
	private Thread getThread(JedisPubSub publisher,Set<HostAndPort> jedisClusterNodes,String clave) {
		return new Thread(() -> {
				try(JedisCluster jedisc = new JedisCluster(jedisClusterNodes);){
					jedisc.subscribe(publisher, clave);	
				} catch (Exception e) {
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
				}			
		});
	}
	
	private JedisPubSub getPublisher() {
		return new JedisPubSub() {
    	    @Override
    	    public void onMessage(String channel, String message) {
    	    	try {
					handler.raiseEvent(channel,message);
				} catch (DomainException e) {	
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), LISTENER));
				}	
    	    }
    	};
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
		return new KeyValue<>(redisprops.getProperty("hostname"), port);
	}
	protected void stopListener() {
		this.someCondition = true;
	}
}
