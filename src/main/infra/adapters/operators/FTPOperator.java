package main.infra.adapters.operators;

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
import main.domain.annotations.Operator;
import main.domain.core.BaseOperator;
import main.domain.exceptions.DomainException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;


@Operator(args={"host","port","ftpUser","ftpPass","commands"})
public class FTPOperator extends BaseOperator implements Callable<List<String>> {

	@Override
	public List<String> call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		
		try {
			var ftp = new FTPClient();
			ftp.connect(this.args.getProperty("host"),Integer.parseInt(this.args.getProperty("port")));
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new DomainException("host not resolved");
			}
			ftp.login(this.args.getProperty("ftpUser"), this.args.getProperty("ftpPass"));
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			log.debug(this.getClass()+" end "+this.name);

			
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
					var result = this.list(ftp, cmd[1]);
					results.add(result.toString());
					break;
				case "upload":
					this.upload(null, cmd[1], cmd[2]);
					results.add(status1.toString());
					break;
				case "download":
					this.download(null, cmd[1], cmd[2]);
					results.add(status1.toString());
					break;
				}
			}
			this.disconnect(ftp);
			return results;
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
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
			throw new DomainException(e.getMessage());
		}
	}

	private void upload(FTPClient ftp,String fileInput,String remoteFilePath) throws IOException {
		File file = new File(fileInput);
		InputStream input = new FileInputStream(file);
		ftp.storeFile(remoteFilePath, input);
		ftp.enterLocalPassiveMode();	
	}

	
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"host\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"port\",type:\"number\"}"));
		params.put(new JSONObject("{name:\"ftpUser\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"ftpPass\",type:\"password\"}"));
		params.put(new JSONObject("{name:\"commands\",type:\"sourcecode\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.FTPOperator");
		tag.put("name", "FTPOperator");
		tag.put("params", params);
		tag.put("opt", new JSONArray());

		return tag;
	}
	@Override
	public String getIconImage() {
		return "ftp.png";
	}
	
}
