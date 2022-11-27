package main.infra.adapters.operators;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.concurrent.Callable;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import main.domain.annotations.Operator;
import main.domain.types.OperatorStage;

@Operator(args={"host","user","pwd","port", "cmd"})
public class SshOperator extends OperatorStage implements Callable<String> {

	@Override
	public String call() throws Exception {		
		JSch jsch = new JSch();		
		Session session = jsch.getSession(this.args.getProperty("user"), this.args.getProperty("host"), Integer.parseInt(this.args.getProperty("port")));
		session.setPassword(this.args.getProperty("pwd"));
		session.connect();
		ChannelExec channel = (ChannelExec) session.openChannel("shell");
		channel.setCommand("pwd");

		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();

		InputStream in = channel.getInputStream();
		InputStream err = channel.getExtInputStream();

		channel.connect();

		byte[] tmp = new byte[1024];
		while (true) {
		    while (in.available() > 0) {
		        int i = in.read(tmp, 0, 1024);
		        if (i < 0) break;
		        outputBuffer.write(tmp, 0, i);
		    }
		    while (err.available() > 0) {
		        int i = err.read(tmp, 0, 1024);
		        if (i < 0) break;
		        errorBuffer.write(tmp, 0, i);
		    }
		    if (channel.isClosed()) {
		        if ((in.available() > 0) || (err.available() > 0)) continue; 
		        break;
		    }
		    try { 
		      Thread.sleep(1000);
		    } catch (Exception ee) {
		    }
		}
		channel.disconnect();
		if(!errorBuffer.toString("UTF-8").equals("")) {
			throw new Exception(errorBuffer.toString("UTF-8"));
		}
		return outputBuffer.toString("UTF-8");
	}

}
