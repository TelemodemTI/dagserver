package main.application.ports.input;

import java.util.Properties;

import main.domain.exceptions.DomainException;

public interface RedisChannelUseCase {
	Properties getRedisChannelProperties() throws DomainException;
	void raiseEvent(String channel, String message);
}
