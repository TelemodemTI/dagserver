package main.cl.dagserver.infra.adapters.operators;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.json.JSONObject;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import javax.jms.*;
import java.util.Iterator;
import java.util.List;

@Operator(args = {"mode", "brokerURL" , "queueName"}, optionalv = {"xcom","timeout"})
public class ActiveMQOperator extends OperatorStage {

	@Override
    public DataFrame call() throws DomainException {
        String mode = this.args.getProperty("mode");
        if ("produce".equalsIgnoreCase(mode)) {
            produce();
            return OperatorStage.createStatusFrame("ok");
        } else if ("consume".equalsIgnoreCase(mode)) {
        	return consume();
        } else {
            throw new IllegalArgumentException("Unsupported mode: " + mode);
        }
    }

	private void produce() throws DomainException {
        try {
            String brokerURL = this.args.getProperty("brokerURL");
            String queueName = this.args.getProperty("queueName");
            String xcomname = this.optionals.getProperty("xcom");
            if(xcomname != null && !xcomname.isEmpty()) {
				if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
				}
            }            
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(destination);
            DataFrame data = (DataFrame) this.xcom.get(xcomname);
            data.iterator().forEachRemaining(row -> {
            	JSONObject jsonObject = new JSONObject();
	            for (String columnName : data.getColumnsIndex()) {
	                jsonObject.put(columnName, row.get(columnName));
	            }
            });
            
            for (Iterator<RowProxy> iterator = data.iterator(); iterator.hasNext();) {
    				var row = iterator.next();
    				JSONObject jsonObject = new JSONObject();
    				TextMessage message = new ActiveMQTextMessage();
    				for (String columnName : data.getColumnsIndex()) {
    	                jsonObject.put(columnName, row.get(columnName));
    	            }
                    message.setText(jsonObject.toString());
                    producer.send(message);
            }
            log.debug("Message sent to queue: " + queueName);
            connection.close();
	    } catch (Exception e) {
	        throw new DomainException(e);
	    } 
    }

	private DataFrame consume() throws DomainException {
        try {
            String brokerURL = this.args.getProperty("brokerURL");
            String queueName = this.args.getProperty("queueName");
            String timeoutStr = this.optionals.getProperty("timeout");
            Integer timeout = 10000;
            if(timeoutStr != null && !timeoutStr.isEmpty()) {
            	timeout = Integer.parseInt(timeoutStr);
            }
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerURL);
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(destination);
            var appender = DataFrame.byArrayRow("messageId","text","jmsExpiration","jmsCorrelationID","jmsPriority","jmsReplyTo").appender();
            while (true) {
                    Message message = consumer.receive(timeout);
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        appender.append(textMessage.getJMSMessageID(),textMessage.getText(),textMessage.getJMSExpiration(),textMessage.getJMSCorrelationID(),textMessage.getJMSPriority(),textMessage.getJMSReplyTo());
                    } else if (message == null) {
                        break; // Salir si no hay más mensajes
                    }
            }
            return appender.toDataFrame();
        } catch (Exception e) {
            throw new DomainException(e);
        }
    }

    @Override
    public String getIconImage() {
        return "activemq.png";
    }

    @Override
    public JSONObject getMetadataOperator() {
        MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.ActiveMQOperator");
        metadata.setType("MQ");        
        metadata.setParameter("mode", "list", List.of("produce", "consume"));
        metadata.setParameter("queueName", "text");
        metadata.setParameter("brokerURL","text");
        metadata.setOpts("xcom","xcom");
        metadata.setOpts("timeout","number");
        return metadata.generate();
    }
    
}
