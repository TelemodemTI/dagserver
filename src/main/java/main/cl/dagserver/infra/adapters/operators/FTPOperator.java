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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.nhl.dflib.DataFrame;

@Operator(args={"host","port","credentials","commands"})
public class FTPOperator extends OperatorStage {
	private CredentialsDTO credentials = null;
	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {		
		DataFrame df = null;
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		if(appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			this.credentials = handler.getCredentials(this.args.getProperty("credentials"));
		}
		if(this.credentials == null) {
			throw new DomainException(new Exception("invalid credentials entry in keystore"));
		}
		try {
			var ftp = new FTPClient();
			ftp.connect(this.getInputProperty("host"),Integer.parseInt(this.getInputProperty("port")));
			int reply = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(reply)) {
				ftp.disconnect();
				throw new DomainException(new Exception("host not resolved"));
			}
			
			
			ftp.login(this.credentials.getUsername(), this.credentials.getPassword());
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			ftp.enterLocalPassiveMode();
			log.debug(this.getClass()+" end "+this.name);

			List<String> comds = Arrays.asList(this.getInputProperty("commands").split(";"));
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					df = this.list(ftp, cmd[1]);
					break;
				case "upload":
					this.upload(ftp, cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				case "download":
					this.download(ftp, cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				 default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
			this.disconnect(ftp);
			return df;
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
	
	private DataFrame list(FTPClient ftp,String directory) throws IOException {
		ftp.cwd(directory);
		FTPFile[] files = ftp.listFiles();
		List<Map<String, Object>> content = new ArrayList<>();
		for (FTPFile file : files) {
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("filename", file.getName());
			map.put("size",file.getSize());
			content.add(map);
		}
		ftp.enterLocalPassiveMode();
		return DataFrameUtils.buildDataFrameFromMap(content);	
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
		metadata.setType("REMOTE");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("credentials", "credentials");
		metadata.setParameter("commands", "remote");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "ftp.png";
	}
	
}
