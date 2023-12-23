package main.cl.dagserver.infra.adapters.operators;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


@Operator(args={"host","port","ftpUser","ftpPass","commands"})
public class FTPOperator extends OperatorStage implements Callable<List<Object>> {

	@Override
	public List<Object> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		
		try {
			var ftp = new FTPClient();
			ftp.connect(this.args.getProperty("host"),Integer.parseInt(this.args.getProperty("port")));
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new DomainException(new Exception("host not resolved"));
			}
			ftp.login(this.args.getProperty("ftpUser"), this.args.getProperty("ftpPass"));
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			log.debug(this.getClass()+" end "+this.name);

			
			List<Object> results = new ArrayList<>();
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			//list pathremote
			//download local remote
			//upload local remote
			JSONObject status1 = new JSONObject();
			status1.put("status", "ok");
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					var result = this.list(ftp, cmd[1]);
					results.add(result);
					break;
				case "upload":
					this.upload(ftp, cmd[1], cmd[2]);
					results.add(status1);
					break;
				case "download":
					this.download(ftp, cmd[1], cmd[2]);
					results.add(status1);
					break;
				 default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
			this.disconnect(ftp);
			return results;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}	

	private void disconnect(FTPClient ftp) throws IOException {
		if (ftp.isConnected()) {
			ftp.logout();
			ftp.disconnect();
		}	
	}	
	
	private JSONArray list(FTPClient ftp,String directory) throws IOException {
		ftp.cwd(directory);
		FTPFile[] files = ftp.listFiles();
		JSONArray content = new JSONArray();
		for (FTPFile file : files) {
			content.put(file.getName());
		}
		ftp.enterLocalPassiveMode();
		return content;	
	}
	
	private void download(FTPClient ftp, String remoteFilePath, String localPath) throws DomainException {
		ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
		try(OutputStream outputStream = new FileOutputStream(localPath)) {
			ftp.retrieveFile(remoteFilePath, outputStream1);
			outputStream1.writeTo(outputStream);
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}

	private void upload(FTPClient ftp,String remoteFilePath,String fileInput) throws IOException {
		File file = new File(fileInput);
		InputStream input = new FileInputStream(file);
		ftp.storeFile(remoteFilePath, input);
		ftp.enterLocalPassiveMode();	
	}

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.FTPOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("ftpUser", "text");
		metadata.setParameter("ftpPass", "password");
		metadata.setParameter("commands", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "ftp.png";
	}
	
}
