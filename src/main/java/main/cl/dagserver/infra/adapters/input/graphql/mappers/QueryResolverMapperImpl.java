package main.cl.dagserver.infra.adapters.input.graphql.mappers;

import java.util.ArrayList;
import org.springframework.stereotype.Component;
import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.domain.model.ExceptionsDTO;
import main.cl.dagserver.domain.model.FileEntryDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.SessionDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.types.Account;
import main.cl.dagserver.infra.adapters.input.graphql.types.Agent;
import main.cl.dagserver.infra.adapters.input.graphql.types.DirectoryEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.Exceptions;
import main.cl.dagserver.infra.adapters.input.graphql.types.FileEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.KeystoreEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.Property;
import main.cl.dagserver.infra.adapters.input.graphql.types.Session;
import main.cl.dagserver.infra.adapters.input.graphql.types.Uncompiled;

@Component
public class QueryResolverMapperImpl implements QueryResolverMapper {

	@Override
	public Agent toAgent(AgentDTO dto) {
		Agent ag = new Agent();
		ag.setHostname(dto.getHostname());
		ag.setId(dto.getId());
		ag.setName(dto.getName());
		ag.setUpdatedOn(dto.getUpdatedOn());
		return ag;
	}

	@Override
	public Property toProperty(PropertyDTO dto) {
		Property p = new Property();
		p.setDescription(dto.getDescription());
		p.setGroup(dto.getGroup());
		p.setId(dto.getId());
		p.setName(dto.getName());
		p.setValue(dto.getValue());
		return p;
	}

	@Override
	public Uncompiled toUncompiled(UncompiledDTO dto) {
		Uncompiled u = new Uncompiled();
		u.setBin(dto.getBin());
		u.setCreatedDt(dto.getCreatedDt());
		u.setUncompiledId(dto.getUncompiledId());
		return u;
	}

	@Override
	public Account toAccount(UserDTO elt) {
		Account a = new Account();
		a.setId(elt.getId());
		a.setTypeAccount(elt.getTypeAccount());
		a.setUsername(elt.getUsername());
		return a;
	}

	@Override
	public Session toSession(SessionDTO dto) {
		Session session = new Session();
		session.setRefreshToken(dto.getRefreshToken());
		session.setToken(dto.getToken());
		return session;
	}

	@Override
	public DirectoryEntry toDirectoryEntry(DirectoryEntryDTO dto) {
		DirectoryEntry dir = new DirectoryEntry();
		dir.setName(dto.getPath());
		dir.setType("folder");
		dir.setContent(dto.getContent().stream().map(this::toFileEntry).toList());
		return dir;
	}

	@Override
	public FileEntry toFileEntry(FileEntryDTO dto) {
		FileEntry fe = new FileEntry();
		fe.setType(dto.getType());
		fe.setName(dto.getFilename());
		var content = dto.getContent();
		if(content!=null) {
			var list = content.stream().map(this::toFileEntry).toList();
			fe.setContent(list);	
		} else {
			fe.setContent(new ArrayList<>());
		}
		return fe;
	}

	@Override
	public Exceptions toExceptions(ExceptionsDTO elt) {
		Exceptions item = new Exceptions();
		item.setClassname(elt.getClassname());
		item.setEventDt(elt.getEventDt());
		item.setMethod(elt.getMethod());
		item.setStack(elt.getStack());
		return item;
	}

	@Override
	public KeystoreEntry toKeystoreEntry(KeystoreEntryDTO elt) {
		KeystoreEntry entry = new KeystoreEntry();
		entry.setName(elt.getName());
		entry.setType(elt.getType());
		return entry;
	}

}
