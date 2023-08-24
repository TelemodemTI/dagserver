package main.infra.adapters.operators;

import java.io.ByteArrayOutputStream;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.BaseOperator;
import main.domain.exceptions.DomainException;
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


@Operator(args={"host","smbUser","smbPass","smbDomain","smbSharename","commands"})
public class Samba2Operator extends BaseOperator implements Callable<List<String>> {

	private static final String SMBSHARENAME = "smbSharename";
	
	
	@Override
	public List<String> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		try(var client = new SMBClient();) {
			
			Connection connection = client.connect(this.args.getProperty("host"));
		    AuthenticationContext ac = new AuthenticationContext(this.args.getProperty("smbUser"), this.args.getProperty("smbPass").toCharArray(), this.args.getProperty("smbDomain"));
		    var smb2session = connection.authenticate(ac);
		    
		    List<String> results = new ArrayList<>();
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			//list pathremote
			//download remote local
			//upload remote local
			JSONObject status1 = new JSONObject();
			status1.put("status", "ok");
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					var result = this.list(smb2session, cmd[1],this.args.getProperty(SMBSHARENAME));
					results.add(result.toString());
					break;
				case "upload":
					this.upload(null, cmd[1], cmd[2],this.args.getProperty(SMBSHARENAME));
					results.add(status1.toString());
					break;
				case "download":
					this.download(smb2session, cmd[1], cmd[2],this.args.getProperty(SMBSHARENAME));
					results.add(status1.toString());
					break;
				default:
					throw new DomainException("command invalid");
				}
			}
		    
			this.disconnect(smb2session, client);
			return results;
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}

	private void disconnect(Session smb2session,SMBClient client) throws IOException {
		smb2session.getConnection().close();
		smb2session.close();
		client.close();
	}
	

	
	private JSONArray list(Session smb2session , String directory, String smb2sharename) {
		JSONArray lista = new JSONArray(); 
		DiskShare share = (DiskShare) smb2session.connectShare(smb2sharename);
	    for (FileIdBothDirectoryInformation f : share.list(directory,"*")) {
	        lista.put(f.getFileName());
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
			throw new DomainException(e.getMessage());
		}
	}

	private void upload(Session smb2session, String fileInput, String remoteFilePath,String smb2sharename) throws DomainException {
		var file = new java.io.File(fileInput);
		Set<FileAttributes> fileAttributes = new HashSet<>();
	    fileAttributes.add(FileAttributes.FILE_ATTRIBUTE_NORMAL);
	    Set<SMB2CreateOptions> createOptions = new HashSet<>();
	    createOptions.add(SMB2CreateOptions.FILE_RANDOM_ACCESS);
	    DiskShare share = (DiskShare) smb2session.connectShare(smb2sharename);
		try(
				InputStream input = new FileInputStream(file);
				File f = share.openFile(remoteFilePath, new HashSet<AccessMask>(Arrays.asList(new AccessMask[]{AccessMask.MAXIMUM_ALLOWED})), fileAttributes, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OVERWRITE_IF, createOptions);
				) {
		    OutputStream oStream = f.getOutputStream();		
			
		    byte[] buffer = new byte[1024];
		    int len;
		    while ((len = input.read(buffer)) != -1) {
		    	oStream.write(buffer, 0, len);
		    }
		    oStream.flush();
		    input.close();
		    oStream.close();	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();

		params.put(new JSONObject("{name:\"host\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"smbUser\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"smbPass\",type:\"password\"}"));
		params.put(new JSONObject("{name:\"smbDomain\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"smbSharename\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"commands\",type:\"sourcecode\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.Samba2Operator");
		tag.put("name", "Samba2Operator");
		tag.put("params", params);
		tag.put("opt", new JSONArray());

		return tag;
	}
	@Override
	public String getIconImage() {
		return "smb.jpg";
	}
	
}
