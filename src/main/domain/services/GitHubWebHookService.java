package main.domain.services;

import java.util.Iterator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Service;

import main.application.ports.input.GitHubWebHookUseCase;
import main.domain.core.BaseServiceComponent;
import main.domain.exceptions.DomainException;
import main.domain.model.ChannelPropsDTO;
import main.domain.model.PropertyParameterDTO;

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
		var propertyList = repository.getProperties(name);
		String dagname = "";
		String jarname = "";
		for (Iterator<PropertyParameterDTO> iterator = propertyList.iterator(); iterator.hasNext();) {
			PropertyParameterDTO propertyParameterDTO = iterator.next();
			if(propertyParameterDTO.getName().equals("dagname")) {
				dagname = propertyParameterDTO.getValue();
			}
			if(propertyParameterDTO.getName().equals("jarname")) {
				jarname = propertyParameterDTO.getValue();
			}
		}
		if(!dagname.isEmpty() && !jarname.isEmpty()) {
			scanner.init().execute(jarname, dagname);	
		}
	}
	

}
