package main.cl.dagserver.infra.adapters.input.kafka;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;
import main.cl.dagserver.application.ports.input.KafkaChannelUseCase;
import main.cl.dagserver.domain.core.ExceptionEventLog;
import main.cl.dagserver.domain.exceptions.DomainException;


@Component
@ImportResource("classpath:properties-config.xml")
public class KafkaChannel {

	@Value( "${param.kafka.refresh.timeout}" )
	private Integer kafkaRefresh;
	private Boolean someCondition = false;
	
	private KafkaChannelUseCase handler;
    private ApplicationEventPublisher eventPublisher;
    private List<Map<String,String>> runningConsumers;
	
	@Autowired
	public KafkaChannel(KafkaChannelUseCase handler,ApplicationEventPublisher eventPublisher){
		this.handler = handler;
		this.eventPublisher = eventPublisher;
	}
	
	@PostConstruct
	public void listenerHandler() {
		runningConsumers = new ArrayList<>();
		Thread listener = new Thread(()-> {
            	try {
					runForever();
            	} catch (InterruptedException ie) {
            		Thread.currentThread().interrupt();
            	} catch (Exception e) {
					eventPublisher.publishEvent(new ExceptionEventLog(this, new DomainException(e), "listenerHandler"));
				}
        });
		listener.start(); 
	}

	private void runForever() throws InterruptedException, DomainException {
		Boolean longRunning = true;
    	while(longRunning.equals(Boolean.TRUE)) {
    		Properties kafkaprops = handler.getKafkaChannelProperties();
    		String status = kafkaprops.getProperty("STATUS");
    		if(status != null && status.equals("ACTIVE")){
    			 if (someCondition.equals(Boolean.TRUE)) {
    				longRunning = false;
                 }
    			 String bootstrapServers = kafkaprops.getProperty("bootstrapServers");
    	         String groupId = kafkaprops.getProperty("groupId");
    	         Integer poll = Integer.parseInt(kafkaprops.getProperty("poll"));
    	         
    	         var consumprops = handler.getKafkaConsumers();
    	         Set<Object> keys = consumprops.keySet();
    	         for (Object key : keys) {
    	        	 String topic = (String) key;
    	        	 Properties properties = new Properties();
        	         properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        	         properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        	         properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        	         properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        	         try (Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
        	        	Map<String,String> item = new HashMap<>();
        	 			item.put("topic", topic);
        	 			item.put("groupId", groupId);
        	 			runningConsumers.add(item);
        	        	consumer.subscribe(Collections.singletonList(topic));
        	        	while (longRunning.equals(Boolean.TRUE)) {
        	        		if (someCondition.equals(Boolean.TRUE)) {
        	     				longRunning = false;
        	                }
        	        		ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(poll));
	        	            for (ConsumerRecord<String, String> record : records) {
	        	            	 handler.raiseEvent(topic,record.value());	
	        	            }
        	        	}
        	         }	 
    	         }
    		}
    		Thread.sleep(kafkaRefresh);	
    	}
	}

	public void setSomeCondition(Boolean someCondition) {
		this.someCondition = someCondition;
	}	
}
