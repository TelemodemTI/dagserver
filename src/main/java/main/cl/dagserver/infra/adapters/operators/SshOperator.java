package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.nhl.dflib.DataFrame;

import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;



@Operator(args={"host","user","port", "cmd"},optionalv = { "pwd","knowhostfile","privateKeyFile" })
public class SshOperator extends OperatorStage {

	private static final String KNOWHOSTFILE = "knowhostfile";
	private static final String PRIVATEKEYFILE = "privateKeyFile";
	
	@Override
	public DataFrame call() throws DomainException {		
		try {
			JSch jsch = new JSch();		
			Session session = jsch.getSession(this.args.getProperty("user"), this.args.getProperty("host"), Integer.parseInt(this.args.getProperty("port")));
			if(this.optionals.getProperty("pwd") != null) {
				session.setPassword(this.args.getProperty("pwd"));
			}
			
			if(this.optionals.getProperty(KNOWHOSTFILE) != null) {
				File initialFile = new File(this.optionals.getProperty(KNOWHOSTFILE));
			    InputStream targetStream = new FileInputStream(initialFile);
				String content = this.readFromInputStream(targetStream);
				InputStream is = new ByteArrayInputStream(content.getBytes());
				jsch.setKnownHosts(is);	
			} 
			if(this.optionals.getProperty(PRIVATEKEYFILE) != null) {
				jsch.addIdentity(this.optionals.getProperty(PRIVATEKEYFILE));
			}
			session.connect();
			ChannelExec channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(this.args.getProperty("cmd"));
			List<Map<String,Object>> list = new ArrayList<>();
			list.add(this.sendToChannel(channel));
			return DataFrameUtils.buildDataFrameFromMap(list);
		} catch (InterruptedException ie) {
		    log.error("InterruptedException: ", ie);
		    Thread.currentThread().interrupt();
		    throw new DomainException(ie);
		} catch (Exception e) {
			throw new DomainException(e);
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
	
	private Map<String,Object> sendToChannel(ChannelExec channel) throws IOException, InterruptedException, JSchException {
		Map<String,Object> output = new HashMap<String,Object>();
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
		output.put("stdout", outputBuffer.toString(StandardCharsets.UTF_8));
		output.put("err", errorBuffer.toString(StandardCharsets.UTF_8));
		return output;
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
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.SshOperator");
		metadata.setType("EXTERNAL");
		metadata.setParameter("host", "text");
		metadata.setParameter("user", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("cmd", "sourcecode",Arrays.asList("application/x-sh"));
		metadata.setOpts(KNOWHOSTFILE, "text");
		metadata.setOpts(PRIVATEKEYFILE, "text");
		metadata.setOpts("pwd", "password");
		return metadata.generate();
	}
	
	@Override
	public String getIconImage() {
		return "ssh.png";
	}
	
}
