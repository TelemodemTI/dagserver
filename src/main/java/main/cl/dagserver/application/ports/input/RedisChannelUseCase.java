package main.cl.dagserver.application.ports.input;

import java.util.Properties;

import main.cl.dagserver.domain.exceptions.DomainException;

public interface RedisChannelUseCase {
	Properties getRedisChannelProperties() throws DomainException;
	Properties getRedisListeners() throws DomainException;
	void raiseEvent(String channel, String message) throws DomainException;
}
