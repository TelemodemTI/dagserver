package main.cl.dagserver.infra.adapters.channels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;
import main.cl.dagserver.application.ports.input.RedisChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.channels.ChannelException;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;


@Component
@Log4j2
@ImportResource("classpath:properties-config.xml")
public class RedisChannel extends InputChannel {
	
	@Autowired
	private RedisChannelUseCase handler;

	@Value( "${param.redis.refresh.timeout}" )
	private Integer redisRefresh;
	private static final String LISTENER = "listener";
	@Autowired
	private ApplicationEventPublisher eventPublisher;

	public void runForever() throws ChannelException {
		try {
			boolean keepRunning = true;
	    	while(keepRunning) {
	    		Properties redisprops = handler.getRedisChannelProperties();
	            String status = redisprops.getProperty("STATUS");
	    		if(status != null && status.equals("ACTIVE")){
	    			listenerActive(redisprops);
	    		}
	    		if (someCondition.equals(Boolean.TRUE)) {
	                     keepRunning = false;
	            }
	    		Thread.sleep(redisRefresh);	
		    }	
		} catch (InterruptedException ie) {
            log.error("InterruptedException: ", ie);
            Thread.currentThread().interrupt(); // Vuelve a establecer la interrupción
            throw new ChannelException(ie); // Relanza la excepción para que sea manejada en el método principal
        }  catch (DomainException e) {
			throw new ChannelException(e);
		}
		
	}
	
	private void listenerActive(Properties redisprops) throws DomainException {
		try {
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
		} catch (Exception e) {
			eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "RedisInputListener.listenerActive"));
		}
		
	}
	
	private void listenerCluster(Properties listenersConfs,Properties redisprops,JedisPubSub publisher) {
		for (String clave : listenersConfs.stringPropertyNames()) {
            if(!bindings.containsKey(clave)) {
				Set<HostAndPort> jedisClusterNodes = new HashSet<>();
				List<Pair<String, Integer>> nodes = this.getConnectionInfoCluster(redisprops);
				for (Iterator<Pair<String, Integer>> iterator = nodes.iterator(); iterator.hasNext();) {
					Pair<String, Integer> keyValue = iterator.next();
					jedisClusterNodes.add(new HostAndPort(keyValue.getKey(), keyValue.getValue()));
				}	
				Thread bindingThread = getThread(publisher,jedisClusterNodes,clave);
				bindingThread.start();
				bindings.put(clave,bindingThread);
            }
        }	
	}
	private Thread getThreadAlt(JedisPubSub publisher, String clave, Pair<String, Integer> kv) {
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

	protected List<Pair<String, Integer>> getConnectionInfoCluster(Properties redisprops) {
		List<Pair<String, Integer>> newarr = new ArrayList<>();
		String[] hosts = redisprops.getProperty("hostname").split(";");
		String[] ports = redisprops.getProperty("port").split(";");
		for (int i = 0; i < hosts.length; i++) {
			String string = hosts[i];
			Integer portr = Integer.parseInt(ports[i]);
			Pair<String, Integer> rv = Pair.of(string, portr);
			newarr.add(rv);
		}
		return newarr;
	}
	private Pair<String, Integer> getConectionInfo(Properties redisprops){
		Integer port = Integer.parseInt(redisprops.getProperty("port"));
		return Pair.of(redisprops.getProperty("hostname"), port);
	}
	
	
}
