package main.infra.adapters.operators;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import org.json.JSONArray;
import org.json.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import main.domain.annotations.Operator;
import main.domain.core.BaseOperator;
import main.domain.exceptions.DomainException;



@Operator(args={"host","user","port", "cmd"},optionalv = { "pwd","knowhostfile","privateKeyFile" })
public class SshOperator extends BaseOperator implements Callable<String> {

	
	@Override
	public String call() throws DomainException {		
		try {
			JSch jsch = new JSch();		
			Session session = jsch.getSession(this.args.getProperty("user"), this.args.getProperty("host"), Integer.parseInt(this.args.getProperty("port")));
			if(this.optionals.getProperty("pwd") != null) {
				session.setPassword(this.args.getProperty("pwd"));
			}
			if(this.optionals.getProperty("knowhostfile") != null) {
				InputStream is = new ByteArrayInputStream(this.optionals.getProperty("knowhostfile").getBytes());
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

		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"host\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"user\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"port\",type:\"number\"}"));
		params.put(new JSONObject("{name:\"cmd\",type:\"sourcecode\"}"));
		
		JSONArray opts = new JSONArray();
		opts.put(new JSONObject("{name:\"knowhostfile\",type:\"text\"}"));
		opts.put(new JSONObject("{name:\"privateKeyFile\",type:\"text\"}"));
		opts.put(new JSONObject("{name:\"pwd\",type:\"password\"}"));
		
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.SshOperator");
		tag.put("name", "SshOperator");
		tag.put("params", params);
		tag.put("opt", opts);

		return tag;

	}
	
	@Override
	public String getIconImage() {
		return "ssh.png";
	}
}
