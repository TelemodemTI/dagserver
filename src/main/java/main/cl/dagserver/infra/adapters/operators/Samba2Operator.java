package main.cl.dagserver.infra.adapters.operators;

import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.msfscc.fileinformation.FileStandardInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import com.nhl.dflib.DataFrame;

@Operator(args={"host","credentials","smbDomain","smbSharename","commands"})
public class Samba2Operator extends OperatorStage {

	private static final String SMBSHARENAME = "smbSharename";
	
	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		try(var client = new SMBClient();) {
			ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
			CredentialsDTO credentials = null;
			if(appCtx != null) {
				var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
				credentials = handler.getCredentials(this.args.getProperty("credentials"));	
			}
			if(credentials == null) {
				throw new DomainException(new Exception("invalid credentials entry in keystore"));
			}
			Connection connection = client.connect(this.args.getProperty("host"));
		    AuthenticationContext ac = new AuthenticationContext(credentials.getUsername(), credentials.getPassword().toCharArray(), this.args.getProperty("smbDomain"));
		    var smb2session = connection.authenticate(ac);
		    
		    List<Map<String,Object>> results = new ArrayList<>();
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			//list pathremote
			//download remote local
			//upload remote local
			Map<String,Object> status1 = new HashMap<String,Object>();
			status1.put("status", "ok");
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					var result = this.list(smb2session, cmd[1],this.args.getProperty(SMBSHARENAME));
					results.addAll(result);
					break;
				case "upload":
					this.upload(smb2session, cmd[1], cmd[2],this.args.getProperty(SMBSHARENAME));
					results.add(status1);
					break;
				case "download":
					this.download(smb2session, cmd[1], cmd[2],this.args.getProperty(SMBSHARENAME));
					results.add(status1);
					break;
				default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
		    
			this.disconnect(smb2session, client);
			return DataFrameUtils.buildDataFrameFromMap(results);
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	private void disconnect(Session smb2session,SMBClient client) throws IOException {
		try {
			smb2session.getConnection().close();
			smb2session.close();
			client.close();	
		} catch (Exception e) {
			log.debug("connection close");
		}
	}
	

	
	private List<Map<String,Object>> list(Session smb2session , String directory, String smb2sharename) {
		List<Map<String,Object>> lista = new ArrayList<>(); 
		DiskShare share = (DiskShare) smb2session.connectShare(smb2sharename);
		if(directory.trim().equals("/")) {
			directory = "";
		}
		if(directory.startsWith("/")) {
			directory = directory.substring(1);
		}
	    for (FileIdBothDirectoryInformation f : share.list(directory,"*")) {
	    	Map<String,Object> map = new HashMap<String,Object>();
	        map.put("filename", f.getFileName());
	        map.put("size",f.getEaSize());
	    	lista.add(map);
	    }
	    return lista;
	}

	private void download(Session smb2session, String remoteFilePath, String localPath,String smb2sharename) throws DomainException {
		DiskShare share = (DiskShare) smb2session.connectShare(smb2sharename);
		// This is the maximum number of bytes the server will allow you to read in a single request
		int maxReadSize = smb2session.getConnection().getNegotiatedProtocol().getMaxReadSize();
		
		try(
				OutputStream outputStream = new FileOutputStream(localPath);
				ByteArrayOutputStream bos = new ByteArrayOutputStream(maxReadSize);
				File smbFileRead = share.openFile(remoteFilePath, EnumSet.of(AccessMask.MAXIMUM_ALLOWED), null, SMB2ShareAccess.ALL,SMB2CreateDisposition.FILE_OPEN, null);
				) {
			
			byte[] buffer = new byte[maxReadSize];
			long offset = 0;
			long remaining = smbFileRead.getFileInformation(FileStandardInformation.class).getEndOfFile();
			while(remaining > 0) {
				int amount = remaining > buffer.length ? buffer.length : (int)remaining;
			    int amountRead = smbFileRead.read(buffer, offset, 0, amount);
			    if (amountRead == -1) {
			    	remaining = 0;
			    } else {
			        bos.write(buffer);
			        remaining -= amountRead;
			        offset += amountRead;
			    }
			}	
			bos.writeTo(outputStream);
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	private void upload(Session smb2session, String remoteFilePath,String fileInput,String smb2sharename) throws DomainException {
		if (remoteFilePath.startsWith("/")) {
		   remoteFilePath = remoteFilePath.substring(1);
		}
		var file = new java.io.File(fileInput);
		Set<FileAttributes> fileAttributes = new HashSet<>();
	    fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
	    Set<SMB2CreateOptions> createOptions = new HashSet<>();
	    createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);
	    DiskShare share = (DiskShare) smb2session.connectShare(smb2sharename);
	    var am = new AccessMask[]{AccessMask.MAXIMUM_ALLOWED};
	    try(
				InputStream input = new FileInputStream(file);
	    		
				File f = share.openFile(remoteFilePath, new HashSet<>(Arrays.asList(am)), fileAttributes, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OVERWRITE_IF, createOptions);
	    		OutputStream oStream = f.getOutputStream();
	    		) {
			
		    byte[] buffer = new byte[1024];
		    int len;
		    while ((len = input.read(buffer)) != -1) {
		    	oStream.write(buffer, 0, len);
		    }
		    oStream.flush();
		    
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.Samba2Operator");
		metadata.setType("REMOTE");
		metadata.setParameter("host", "text");
		metadata.setParameter("credentials", "credentials");
		metadata.setParameter("smbDomain", "text");
		metadata.setParameter(SMBSHARENAME, "text");
		metadata.setParameter("commands", "remote");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "smb.jpg";
	}
}
