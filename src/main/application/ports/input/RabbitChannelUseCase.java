package main.application.ports.input;

import java.util.Properties;

import main.domain.exceptions.DomainException;

public interface RabbitChannelUseCase {

	Properties getRabbitChannelProperties() throws DomainException;
	void raiseEvent(String bodyStr, String string, String routingKey, String contentType);

}
