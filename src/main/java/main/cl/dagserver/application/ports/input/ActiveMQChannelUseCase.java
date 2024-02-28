package main.cl.dagserver.application.ports.input;

import java.util.Properties;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface ActiveMQChannelUseCase {
	Properties getActiveMQChannelProperties() throws DomainException;
	Properties getActiveMQListeners() throws DomainException;
	void raiseEvent(String queue, String message) throws DomainException;
}
