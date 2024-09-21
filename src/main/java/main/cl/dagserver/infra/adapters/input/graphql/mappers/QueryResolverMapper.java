package main.cl.dagserver.infra.adapters.input.graphql.mappers;

import main.cl.dagserver.domain.model.AgentDTO;
import main.cl.dagserver.domain.model.ChannelDTO;
import main.cl.dagserver.domain.model.ChannelPropsDTO;
import main.cl.dagserver.domain.model.DirectoryEntryDTO;
import main.cl.dagserver.domain.model.ExceptionsDTO;
import main.cl.dagserver.domain.model.FileEntryDTO;
import main.cl.dagserver.domain.model.KeystoreEntryDTO;
import main.cl.dagserver.domain.model.PropertyDTO;
import main.cl.dagserver.domain.model.SessionDTO;
import main.cl.dagserver.domain.model.UncompiledDTO;
import main.cl.dagserver.domain.model.UserDTO;
import main.cl.dagserver.infra.adapters.input.graphql.types.Agent;
import main.cl.dagserver.infra.adapters.input.graphql.types.Property;
import main.cl.dagserver.infra.adapters.input.graphql.types.Session;
import main.cl.dagserver.infra.adapters.input.graphql.types.Uncompiled;
import main.cl.dagserver.infra.adapters.input.graphql.types.Account;
import main.cl.dagserver.infra.adapters.input.graphql.types.Channel;
import main.cl.dagserver.infra.adapters.input.graphql.types.ChannelProps;
import main.cl.dagserver.infra.adapters.input.graphql.types.DirectoryEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.Exceptions;
import main.cl.dagserver.infra.adapters.input.graphql.types.FileEntry;
import main.cl.dagserver.infra.adapters.input.graphql.types.KeystoreEntry;

public interface QueryResolverMapper {	
	public Agent toAgent(AgentDTO dto);	
	public Property toProperty(PropertyDTO dto);
	public Uncompiled toUncompiled(UncompiledDTO dto);
	public Account toAccount(UserDTO elt);
	public Channel toChannel(ChannelDTO dto);
	public ChannelProps toChannelProps(ChannelPropsDTO dto);
	public Session toSession(SessionDTO dto);
	public DirectoryEntry toDirectoryEntry(DirectoryEntryDTO dto);
	public FileEntry toFileEntry(FileEntryDTO dto);
	public Exceptions toExceptions(ExceptionsDTO elt);
	public KeystoreEntry toKeystoreEntry(KeystoreEntryDTO elt);
}
