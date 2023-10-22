package main.cl.dagserver.application.ports.input;

import java.util.Properties;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface RabbitChannelUseCase {

	Properties getRabbitChannelProperties() throws DomainException;
	void raiseEvent(String bodyStr, String string, String routingKey, String contentType) throws DomainException;

}
