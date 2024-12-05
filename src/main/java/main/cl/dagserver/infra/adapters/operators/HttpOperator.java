package main.cl.dagserver.infra.adapters.operators;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import com.nhl.dflib.DataFrame;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
@Operator(args={"url","method","timeout","contentType"},optionalv = {"xcom","authorizationHeader"})
public class HttpOperator extends OperatorStage {

	private static final String AUTHORIZATION_HEADER = "authorizationHeader";
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		try {
			
			String urlStr = this.args.getProperty("url");
			if(urlStr.startsWith("${") && urlStr.endsWith("}")) {
				String xcomheader = urlStr.replace("${", "").replace("}", "");
				if(this.xcom.containsKey(xcomheader)) {
					DataFrame df = (DataFrame) this.xcom.get(xcomheader);
					urlStr = df.getColumn("output").get(0).toString();
				}
			}
			
			URL url = new URL(urlStr);
			disableSSLVerification();

			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(this.args.getProperty("method"));
			
			if(this.optionals.get(AUTHORIZATION_HEADER) != null) {
				String value = this.optionals.getProperty(AUTHORIZATION_HEADER);
				if(value.startsWith("${") && value.endsWith("}")) {
					String xcomheader = value.replace("${", "").replace("}", "");
					if(this.xcom.containsKey(xcomheader)) {
						DataFrame df = (DataFrame) this.xcom.get(xcomheader);
						String header = df.getColumn("output").get(0).toString();
						log.debug("AUTHORIZATION_HEADER "+header.substring(0, 5) + "..." );
						con.setRequestProperty("Authorization", header);	
					}
				} else {
					log.debug("AUTHORIZATION_HEADER "+value.substring(0, 5) + "..." );
					con.setRequestProperty("Authorization", value);	
				}	
			}
			con.setRequestProperty("Content-Type", this.args.getProperty("contentType"));
			Integer timeout = Integer.parseInt(this.args.getProperty("timeout"));
			con.setConnectTimeout(timeout);
			con.setReadTimeout(timeout);
			
			String xcomname = this.optionals.getProperty("xcom");
			if(this.xcom.containsKey(xcomname)) {
				DataFrame df = (DataFrame) this.xcom.get(xcomname);
				String body = df.getColumn(0).get(0).toString();
				con.setDoOutput(true);
				OutputStream os = con.getOutputStream();
				os.write(body.getBytes());
				os.flush();
				os.close();	
			}
			int responseCode = con.getResponseCode();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
			}
			in.close();
			log.debug(this.getClass()+" end "+this.name);
			Map<String,Object> output = new HashMap<>();
			output.put("response", response.toString());
			output.put("responseCode", responseCode);
			return DataFrameUtils.buildDataFrameFromMap(Arrays.asList(output));
		} catch (Exception e) {
			throw new DomainException(e);
		}
	}
	
	private void disableSSLVerification() throws Exception {
	    TrustManager[] trustAllCerts = new TrustManager[] {
	        new X509TrustManager() {
	            public X509Certificate[] getAcceptedIssuers() { return null; }
	            public void checkClientTrusted(X509Certificate[] certs, String authType) {}
	            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
	        }
	    };

	    SSLContext sc = SSLContext.getInstance("SSL");
	    sc.init(null, trustAllCerts, new java.security.SecureRandom());
	    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	    HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.HttpOperator");
		metadata.setType("EXTERNAL");
		metadata.setParameter("url", "text");
		metadata.setParameter("method", "list",Arrays.asList("GET","POST","PUT","DELETE"));
		metadata.setParameter("timeout", "number");
		metadata.setParameter("contentType", "text");
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts(AUTHORIZATION_HEADER, "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "http.png";
	}
	
}
