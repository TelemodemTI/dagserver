package main.application.ports.input;

import main.domain.exceptions.DomainException;
import main.domain.model.ChannelPropsDTO;


public interface GitHubWebHookUseCase {
	ChannelPropsDTO getChannelPropsFromRepo(String repourl) throws DomainException;
	void raiseEvent(String repourl) throws DomainException;
}
