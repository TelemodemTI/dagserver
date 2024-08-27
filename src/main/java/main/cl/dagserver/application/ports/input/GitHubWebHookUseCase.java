package main.cl.dagserver.application.ports.input;


import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.ChannelPropsDTO;

public interface GitHubWebHookUseCase {
	ChannelPropsDTO getChannelPropsFromRepo(String repourl) throws DomainException;
	void raiseEvent(String repourl) throws DomainException;
}
