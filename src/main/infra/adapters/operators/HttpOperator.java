package main.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;

@Operator(args={"url","method","timeout","contentType"},optionalv = {"bodyxcom","authorizationHeader"})
public class HttpOperator extends OperatorStage implements Callable<String> {

	
	@Override
	public String call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		try {
			URL url = new URL(this.args.getProperty("url"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(this.args.getProperty("method"));
			
			if(this.optionals.get("authorizationHeader") != null) {
				con.setRequestProperty("Authorization", this.optionals.getProperty("authorizationHeader"));	
			}
			con.setRequestProperty("Content-Type", this.args.getProperty("contentType"));
			Integer timeout = Integer.parseInt(this.args.getProperty("timeout"));
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			
			int responseCode = con.getResponseCode();
			
			
			String xcomname = this.args.getProperty("bodyxcom");
			if(this.xcom.has(xcomname)) {
				String body = (String) this.xcom.get(xcomname);
				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();
				os.write(body.getBytes());
				os.flush();
				os.close();	
			}
				
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				log.debug(this.getClass()+" end "+this.name);
				return response.toString();	
			} else {
				throw new DomainException("request failed!");
			}
		} catch (Exception e) {
			throw new DomainException(e.getMessage());
		}
	}
	

	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.HttpOperator");
		metadata.setParameter("url", "text");
		metadata.setParameter("method", "list",Arrays.asList("GET","POST","PUT","DELETE"));
		metadata.setParameter("timeout", "number");
		metadata.setParameter("contentType", "text");
		metadata.setOpts("bodyxcom", "text");
		metadata.setOpts("authorizationHeader", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "http.png";
	}
}
