package main.cl.dagserver.infra.adapters.operators;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;

import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.nhl.dflib.DataFrame;

@Operator(args={"host","port","credentials","commands"})
public class WebDAVOperator extends OperatorStage {

	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {		
		DataFrame df = null;
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		CredentialsDTO credentials = null;
		if(appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			credentials = handler.getCredentials(this.args.getProperty("credentials"));	
		}
		if(credentials == null) {
			throw new DomainException(new Exception("invalid credentials entry in keystore"));
		}
		String username = credentials.getUsername();
		String pwd = credentials.getPassword();
		Sardine sardine = null;
		if(username.isEmpty() && pwd.isEmpty()) {
			sardine = SardineFactory.begin();
		} else {
			sardine = SardineFactory.begin(username,pwd);
		}
		try {
			List<String> comds = Arrays.asList(this.args.getProperty("commands").split(";"));
			for (Iterator<String> iterator = comds.iterator(); iterator.hasNext();) {
				String[] cmd = iterator.next().split(" ");
				switch (cmd[0]) {
				case "list":
					df = this.list(sardine,cmd[1]);
					break;
				case "upload":
					this.upload(sardine,cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				case "download":
					this.download(sardine,cmd[1], cmd[2]);
					df = DataFrameUtils.createStatusFrame("ok");
					break;
				 default:
					throw new DomainException(new Exception("command invalid"));
				}
			}
			log.debug(this.getClass()+" end "+this.name);
			return df;
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}	

	private DataFrame list(Sardine sardine, String directory) throws IOException {
		List<Map<String,Object>> list = new ArrayList<>();
		String resource = this.args.getProperty("host") + directory ;
		List<DavResource> resources = sardine.list(resource);
		for (DavResource res : resources){
			Map<String,Object> content = new HashMap<>();
			content.put("name", res.getName());
			content.put("type", res.getContentType());
			content.put("size", res.getContentLength());
			list.add(content);
		}
		return DataFrameUtils.buildDataFrameFromMap(list);	
	}
	
	private void download(Sardine sardine,String remoteFilePath, String localPath) throws IOException {
		String resource = this.args.getProperty("host") + remoteFilePath ;
		InputStream is = sardine.get(resource);
		try (FileOutputStream fos = new FileOutputStream(localPath)) {
	        byte[] buffer = new byte[1024];
	        int bytesRead;
	        while ((bytesRead = is.read(buffer)) != -1) {
	            fos.write(buffer, 0, bytesRead);
	        }
	    } finally {
	        if (is != null) {
	            is.close();
	        }
	    }
	}

	private void upload(Sardine sardine , String remoteFilePath,String fileInput) throws IOException {
		byte[] data = FileUtils.readFileToByteArray(new File(fileInput));
		String resource = this.args.getProperty("host") + remoteFilePath ;
		sardine.put(resource, data);
	}

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.WebDAVOperator");
		metadata.setType("REMOTE");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("credentials", "credentials");
		metadata.setParameter("commands", "remote");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "webdav.png";
	}
	
}
