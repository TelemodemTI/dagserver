package main.cl.dagserver.infra.adapters.operators;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;


@Operator(args={"mode", "bootstrapServers","topic","timeoutSeconds" }, optionalv={ "xcom","poll","groupId" })
public class KafkaOperator extends OperatorStage {

	@Override
	public DataFrame call() throws DomainException {		
		String mode = this.args.getProperty("mode");
        if ("produce".equalsIgnoreCase(mode)) {
        	produce();
        	return DataFrameUtils.createStatusFrame("ok");
        } else {
            return consume();
        } 
	}

	private void produce() throws DomainException {
        try {
            String bootstrapServers = this.args.getProperty("bootstrapServers");
            String topic = this.args.getProperty("topic");
            String xcomname = this.optionals.getProperty("xcom");
            Long timeout = Long.parseLong(this.args.getProperty("timeoutSeconds"));
            Properties properties = new Properties();
            properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            if(xcomname != null && !xcomname.isEmpty()) {
				if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
				}
            }
            DataFrame df = (DataFrame) this.xcom.get(xcomname);
            
            for (Iterator<RowProxy> iterator = df.iterator(); iterator.hasNext();) {
				RowProxy map = iterator.next();
				try (Producer<String, String> producer = new KafkaProducer<>(properties)) {
					String message = new JSONObject(map).toString();
	                ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
	                producer.send(record).get(timeout, TimeUnit.SECONDS);
	            }
			}
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
	private DataFrame consume() throws DomainException {
        try {
            String bootstrapServers = this.args.getProperty("bootstrapServers");
            String groupId = this.optionals.getProperty("groupId");
            String topic = this.args.getProperty("topic");
            Integer poll = Integer.parseInt(this.optionals.getProperty("poll"));
            List<Map<String,Object>> rv = new ArrayList<>();
            Properties properties = new Properties();
            properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            if(groupId != null && !groupId.isEmpty()) {
            	properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);	
            }
            properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

            Long timeoutL = Long.parseLong(this.args.getProperty("timeoutSeconds"));
            Duration timeout = Duration.ofSeconds(timeoutL); 
            
            try (Consumer<String, String> consumer = new KafkaConsumer<>(properties)) {
                List<PartitionInfo> partitions = consumer.partitionsFor(topic,timeout);
                List<TopicPartition> partitionsToAssign = new ArrayList<>();
                for (PartitionInfo partition : partitions) {
                    partitionsToAssign.add(new TopicPartition(partition.topic(), partition.partition()));
                }
                consumer.assign(partitionsToAssign);
                consumer.seekToBeginning(partitionsToAssign);
                while (true) {
                	ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(poll));
                	if (records.isEmpty()) {
                        break;  
                    }
                    for (ConsumerRecord<String, String> record : records) {
                    	Map<String,Object> map = new HashMap<String,Object>();
                        map.put("message", record.value());
                    	rv.add(map);
                    }	
                }
                return DataFrameUtils.buildDataFrameFromMap(rv);
            }
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.KafkaOperator");
		metadata.setType("MQ");
		metadata.setParameter("mode", "list",Arrays.asList("consume","produce"));
		metadata.setParameter("bootstrapServers", "text");
		metadata.setParameter("topic", "text");
		metadata.setParameter("timeoutSeconds", "number");
		metadata.setOpts("xcom","xcom");
		metadata.setOpts("poll", "number");
		metadata.setOpts("groupId", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "kafka.png";
	}
	
}
