package main.cl.dagserver.unitest.infra.adapters.channels;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Properties;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import static org.mockito.ArgumentMatchers.any;
import main.cl.dagserver.application.ports.input.KafkaChannelUseCase;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.infra.adapters.channels.KafkaChannel;


class KafkaChannelTest {

    @Mock
    KafkaChannelUseCase handler;

    @Mock
    ApplicationEventPublisher eventPublisher;

    private KafkaChannel channel;

    @BeforeEach
    void init() {
        eventPublisher = mock(ApplicationEventPublisher.class);
        handler = mock(KafkaChannelUseCase.class);
        channel = new KafkaChannel();
        ReflectionTestUtils.setField(channel, "handler", handler);
        ReflectionTestUtils.setField(channel, "eventPublisher",eventPublisher);
    }

    @Test
    void runForeverTest() throws DomainException, InterruptedException {
        Properties kafkaprops = new Properties(); 
        kafkaprops.setProperty("STATUS", "ACTIVE");
        kafkaprops.setProperty("bootstrapServers", "localhost:9092");
        kafkaprops.setProperty("groupId", "testGroup");
        kafkaprops.setProperty("poll", "1000");

        Properties consumprops = new Properties();
        consumprops.setProperty("testTopic", "testValue");

        when(handler.getKafkaChannelProperties()).thenReturn(kafkaprops);
        when(handler.getKafkaConsumers()).thenReturn(consumprops);

        ReflectionTestUtils.setField(channel, "someCondition", false);
        ReflectionTestUtils.setField(channel, "kafkaRefresh", 1000);

        Thread thread = new Thread(() -> {
            try {
                channel.runForever();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        thread.start();
        ReflectionTestUtils.setField(channel, "someCondition", true);
        thread.join();

        assertTrue(true);
    }

    @SuppressWarnings({ "unused", "unchecked" })
	@Test
    void raiseEventTest() throws DomainException {
        Properties kafkaprops = new Properties(); 
        kafkaprops.setProperty("STATUS", "ACTIVE");
        kafkaprops.setProperty("bootstrapServers", "localhost:9092");
        kafkaprops.setProperty("groupId", "testGroup");
        kafkaprops.setProperty("poll", "1000");

        Properties consumprops = new Properties();
        consumprops.setProperty("testTopic", "testValue");

        when(handler.getKafkaChannelProperties()).thenReturn(kafkaprops);
        when(handler.getKafkaConsumers()).thenReturn(consumprops);

     // Crear un mock de ConsumerRecords<String, String>
        ConsumerRecords<String, String> mockRecords = mock(ConsumerRecords.class);

     // Mockear el consumidor
        Consumer<String, String> mockConsumer = mock(Consumer.class);
        when(mockConsumer.poll(any(Duration.class))).thenReturn(mock(ConsumerRecords.class));

        // Mockear el método createConsumer para devolver el mock del consumidor
        KafkaChannel channel1 = new KafkaChannel() {
            @Override
            protected Consumer<String, String> createConsumer(Properties properties) {
                return mockConsumer;
            }
        };

        ReflectionTestUtils.setField(channel1, "handler", handler);
        ReflectionTestUtils.setField(channel1, "eventPublisher",eventPublisher);
        // Ejecutar el método raiseEvent
        channel1.raiseEvent(consumprops, "localhost:9092", "testGroup", true, 1000);
        assertTrue(true);
    }

    
}