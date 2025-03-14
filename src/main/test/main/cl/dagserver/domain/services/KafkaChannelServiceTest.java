package main.cl.dagserver.domain.services;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import main.cl.dagserver.application.ports.output.SchedulerRepositoryOutputPort;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

class KafkaChannelServiceTest {

	private KafkaChannelService service = new KafkaChannelService();
	
	@Mock
	private SchedulerRepositoryOutputPort repository;
	
	@BeforeEach
    void init() {
        repository = mock(SchedulerRepositoryOutputPort.class);
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(service, "kafkaPropkey", "kafkaPropkey");
    }
	
	@Test
	void getKafkaChannelPropertiesTest() throws DomainException {
		PropertyParameterDTO dto = new PropertyParameterDTO();
		dto.setValue("kafka_consumer_listener");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(dto);
		when(repository.getProperties(anyString())).thenReturn(props);
		var proprv = service.getKafkaChannelProperties();
		assertNotNull(proprv);
	}
	@Test
	void getKafkaChannelErrorPropertiesTest() throws DomainException {
		PropertyParameterDTO dto = new PropertyParameterDTO();
		dto.setName("name");
		dto.setValue("testerror");
		List<PropertyParameterDTO> props = new ArrayList<>();
		props.add(dto);
		when(repository.getProperties(anyString())).thenReturn(props);
		var proprv = service.getKafkaChannelProperties();
		assertNotNull(proprv);
	}
	@Test
	void getKafkaConsumersTest() throws DomainException {
		PropertyParameterDTO dto = new PropertyParameterDTO();
		dto.setValue("kafka_consumer_listener");
		dto.setName("name");
		List<PropertyParameterDTO> prop1 = new ArrayList<>();
		prop1.add(dto);
		when(repository.getProperties(anyString())).thenReturn(prop1);
		var props = service.getKafkaConsumers();
		assertNotNull(props);
	}
	@Test
    void raiseEventTest() throws DomainException {
        String expectedChannel = "testChannel";
        String expectedMessage = "testMessage";
        service.raiseEvent(expectedChannel, expectedMessage);
        assertTrue(true);
    }
}