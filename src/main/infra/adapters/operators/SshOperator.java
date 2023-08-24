package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;



@Operator(args={"host","user","port", "cmd"},optionalv = { "pwd","knowhostfile","privateKeyFile" })
public class SshOperator extends OperatorStage implements Callable<String> {

	
	@Override
	public String call() throws DomainException {		
		try {
			JSch jsch = new JSch();		
			Session session = jsch.getSession(this.args.getProperty("user"), this.args.getProperty("host"), Integer.parseInt(this.args.getProperty("port")));
			if(this.optionals.getProperty("pwd") != null) {
				session.setPassword(this.args.getProperty("pwd"));
			}
			if(this.optionals.getProperty("knowhostfile") != null) {
				File initialFile = new File(this.optionals.getProperty("knowhostfile"));
			    InputStream targetStream = new FileInputStream(initialFile);
				String content = this.readFromInputStream(targetStream);
				InputStream is = new ByteArrayInputStream(content.getBytes());
				jsch.setKnownHosts(is);	
			}
			if(this.optionals.getProperty("privateKeyFile") != null) {
				jsch.addIdentity(this.optionals.getProperty("privateKeyFile"));
			}
			session.connect();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(this.args.getProperty("cmd"));
			return this.sendToChannel(channel);
		} catch (InterruptedException ie) {
		    log.error("InterruptedException: ", ie);
		    Thread.currentThread().interrupt();
		    return null;
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	
	private String readFromInputStream(InputStream inputStream)
			  throws IOException {
			    StringBuilder resultStringBuilder = new StringBuilder();
			    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
			        String line;
			        while ((line = br.readLine()) != null) {
			            resultStringBuilder.append(line).append("\n");
			        }
			    }
			  return resultStringBuilder.toString();
			}
	
	private String sendToChannel(ChannelExec channel) throws IOException, InterruptedException, DomainException, JSchException {
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();

		InputStream in = channel.getInputStream();
		InputStream err = channel.getExtInputStream();
		channel.connect();
		byte[] tmp = new byte[1024];
		while (true) {
			this.writeToOut(in, outputBuffer, tmp);
		    this.writeToOut(err, errorBuffer, tmp);
		    if (channel.isClosed() && !((in.available() > 0) || (err.available() > 0))) {	
		    	break;
		    }
		    Thread.sleep(1000);
		}
		channel.disconnect();
		if(!errorBuffer.toString(StandardCharsets.UTF_8).equals("")) {
			throw new DomainException(errorBuffer.toString(StandardCharsets.UTF_8));
		}
		return outputBuffer.toString(StandardCharsets.UTF_8);
	}
	
	private void writeToOut(InputStream in,ByteArrayOutputStream outputBuffer,byte[] tmp) throws IOException {
		 while (in.available() > 0) {
		        int i = in.read(tmp, 0, 1024);
		        if (i < 0) break;
		        outputBuffer.write(tmp, 0, i);
		 }
	}
	
	@Override
	public JSONObject getMetadataOperator() {	
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.SshOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("user", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("cmd", "sourcecode");
		metadata.setOpts("knowhostfile", "text");
		metadata.setOpts("privateKeyFile", "text");
		metadata.setOpts("pwd", "password");
		return metadata.generate();
	}
	
	@Override
	public String getIconImage() {
		return "ssh.png";
	}
}
