package main.infra.adapters.operators;

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
import main.domain.core.DagExecutable;
import main.domain.exceptions.DomainException;
import main.infra.adapters.input.graphql.types.OperatorStage;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodCall;

@Operator(args={"host","user","pwd","port", "cmd"})
public class SshOperator extends OperatorStage implements Callable<String> {

	
	@Override
	public String call() throws DomainException {		
		try {
			JSch jsch = new JSch();		
			Session session = jsch.getSession(this.args.getProperty("user"), this.args.getProperty("host"), Integer.parseInt(this.args.getProperty("port")));
			session.setPassword(this.args.getProperty("pwd"));
			session.connect();
			ChannelExec channel = (ChannelExec) session.openChannel("shell");
			channel.setCommand("pwd");
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
		    if (channel.isClosed()) {
		        if ((in.available() > 0) || (err.available() > 0)) continue; 
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
	public Implementation getDinamicInvoke(String stepName,String propkey, String optkey) throws DomainException {
		try {
			Implementation implementation = MethodCall.invoke(DagExecutable.class.getDeclaredMethod("addOperator", String.class, Class.class, String.class)).with(stepName, SshOperator.class,propkey);
			return implementation;	
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
    }
	@Override
	public JSONObject getMetadataOperator() {
		JSONArray params = new JSONArray();
		params.put(new JSONObject("{name:\"host\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"user\",type:\"text\"}"));
		params.put(new JSONObject("{name:\"pwd\",type:\"password\"}"));
		params.put(new JSONObject("{name:\"port\",type:\"number\"}"));
		params.put(new JSONObject("{name:\"cmd\",type:\"sourcecode\"}"));
		JSONObject tag = new JSONObject();
		tag.put("class", "main.infra.adapters.operators.SshOperator");
		tag.put("name", "SshOperator");
		tag.put("params", params);
		return tag;
	}
	public String getIconImage() {
		return "ssh.png";
	}
}
