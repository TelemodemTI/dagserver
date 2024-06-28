package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.json.JSONObject;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;



@Operator(args={"host","port","sftpUser","sftpPass","commands"})
public class SFTPOperator extends OperatorStage  {

	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);

		try {
			JSch ssh = new JSch();
			Session session = ssh.getSession(this.args.getProperty("sftpUser"), this.args.getProperty("host"),Integer.parseInt(this.args.getProperty("port")));
			session.setPassword(this.args.getProperty("sftpPass"));
			Properties config = new Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			Channel channel = session.openChannel("sftp");
			channel.connect();
			ChannelSftp sftp = (ChannelSftp) channel;
			
			List<Map<String,Object>> results = new ArrayList<>();
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			Map<String,Object> status1 = new HashMap<String,Object>();
			status1.put("status", "ok");
			
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					var result = this.list(sftp, cmd[1]);
					results.addAll(result);
					break;
				case "upload":
					this.upload(sftp, cmd[1], cmd[2]);
					results.add(status1);
					break;
				case "download":
					this.download(sftp, cmd[1], cmd[2]);
					results.add(status1);
					break;
				default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
			
			this.disconnect(sftp);
			return OperatorStage.buildDataFrame(results);
		} catch (Exception e) {
			throw new DomainException(e);
		}	
	}
	
	private List<Map<String,Object>> list(ChannelSftp sftp,String directory) throws SftpException {
		List<?> vect = sftp.ls(directory);
		List<Map<String,Object>> content = new ArrayList<>();
		for (Iterator<?> iterator = vect.iterator(); iterator.hasNext();) {
			LsEntry entry = (LsEntry) iterator.next();
			Map<String,Object> mp = new HashMap<String,Object>();
			mp.put("filename", entry.getFilename());
			mp.put("longname", entry.getLongname());
			content.add(mp);
		}
		return content;	
	}


	private void download(ChannelSftp sftp , String remoteFilePath , String localPath) throws DomainException {
		try(
				OutputStream outputStream = new FileOutputStream(localPath);
				BufferedInputStream bis = new BufferedInputStream(sftp.get(remoteFilePath));
				ByteArrayOutputStream os = new ByteArrayOutputStream();
			) {
			byte[] buffer = new byte[1024];
			int readCount;
			while ((readCount = bis.read(buffer)) > 0) {
				os.write(buffer, 0, readCount);
			}
			os.writeTo(outputStream);
		} catch (Exception e) {
			throw new DomainException(e);
		}	
	}

	
	private void upload(ChannelSftp sftp , String remoteFilePath, String fileInput) throws DomainException {
		try {
			File file = new File(fileInput);
			InputStream input = new FileInputStream(file);
			sftp.put(input, remoteFilePath);	
		} catch (Exception e) {
			throw new DomainException(e);
		}
		
	}
	
	
	public void disconnect(ChannelSftp sftp) {
		if(sftp.isConnected()){
			sftp.disconnect();	
		}	
	}
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.SFTPOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("sftpUser", "text");
		metadata.setParameter("sftpPass", "password");
		metadata.setParameter("commands", "remote");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "sftp.png";
	}
	
}
