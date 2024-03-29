package main.cl.dagserver.infra.adapters.input.channels.kafka;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.input.channels.InputChannel;


@Component
@ImportResource("classpath:properties-config.xml")
public class KafkaChannel extends InputChannel {

	@Value( "${param.kafka.refresh.timeout}" )
	private Integer kafkaRefresh;	
	private KafkaChannelUseCase handler;
    private List<Map<String,String>> runningConsumers;
	
	@Autowired
	public KafkaChannel(KafkaChannelUseCase handler,ApplicationEventPublisher eventPublisher){
		super(eventPublisher);
		this.handler = handler;
	}
	

	public void runForever() throws InterruptedException, DomainException {
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
    	         this.raiseEvent(consumprops, bootstrapServers, groupId, longRunning, poll);
    		}
    		Thread.sleep(kafkaRefresh);	
    	}
	}

	private void raiseEvent(Properties consumprops,String bootstrapServers,String groupId, Boolean longRunning,Integer poll) throws DomainException {
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
   	            for (ConsumerRecord<String, String> recorda : records) {
   	            	 handler.raiseEvent(topic,recorda.value());	
   	            }
	        	}
	         }	 
        }
	}
	
}
