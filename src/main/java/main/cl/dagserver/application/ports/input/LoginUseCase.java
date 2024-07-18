package main.cl.dagserver.application.ports.input;

import main.cl.dagserver.domain.model.SessionDTO;

public interface LoginUseCase {
	public SessionDTO apply(String token);
}
