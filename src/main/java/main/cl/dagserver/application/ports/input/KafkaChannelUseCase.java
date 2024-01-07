package main.cl.dagserver.application.ports.input;

import java.util.Properties;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface KafkaChannelUseCase {
	
	void raiseEvent(String topic, String message) throws DomainException;
	Properties getKafkaChannelProperties() throws DomainException;
	Properties getKafkaConsumers() throws DomainException;
} 
