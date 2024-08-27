package main.cl.dagserver.domain.services;

import java.util.Iterator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.cl.dagserver.application.ports.input.GitHubWebHookUseCase;
import main.cl.dagserver.domain.core.BaseServiceComponent;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.PropertyParameterDTO;

@Service
@ImportResource("classpath:properties-config.xml")
public class GitHubWebHookService extends BaseServiceComponent implements GitHubWebHookUseCase {

	@Value( "${param.git_hub.propkey}" )
	private String gitHubPropkey;
	
	@Override
	public ChannelPropsDTO getChannelPropsFromRepo(String repourl) throws DomainException {
		ChannelPropsDTO prop1 = new ChannelPropsDTO();
		var propertyList = repository.getProperties(gitHubPropkey);
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getDescription().equals(repourl)){
				prop1.setKey(propertyParameterDTO.getName());
				prop1.setDescr(propertyParameterDTO.getDescription());
				prop1.setValue(propertyParameterDTO.getValue());
				break;
			}
		}
		return prop1;
	}

	@Override
	public void raiseEvent(String repourl) throws DomainException {
		ChannelPropsDTO repos = this.getChannelPropsFromRepo(repourl);
		String name = repos.getKey();
		this.trigggerEvent(name, "GITHUB_EVENT",repourl);
	}
	

}
