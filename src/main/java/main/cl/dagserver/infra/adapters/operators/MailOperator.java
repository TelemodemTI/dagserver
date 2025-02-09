package main.cl.dagserver.infra.adapters.operators;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.json.JSONObject;
import org.springframework.context.ApplicationContext;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.row.RowProxy;
import main.cl.dagserver.application.ports.input.InternalOperatorUseCase;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.DataFrameUtils;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
import main.cl.dagserver.domain.model.CredentialsDTO;
import main.cl.dagserver.infra.adapters.confs.ApplicationContextUtils;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.BodyPart;
import javax.mail.Multipart;


@Operator(args={"host","port","credentials","fromMail","toEmail","subject","protocol"},optionalv = {"body","xcom","attachedFilename","stepAttachedFilename","ccList"})
public class MailOperator extends OperatorStage {

	private static final String FROMMAIL = "fromMail";
	private CredentialsDTO credentials = null;
	@SuppressWarnings("static-access")
	@Override
	public DataFrame call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		
		Properties props = new Properties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", this.args.getProperty("host")); //SMTP Host
		props.put("mail.smtp.ssl.trust", this.args.getProperty("host"));
		if(!this.args.getProperty("protocol").equals("plaintext")) {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.ssl.protocols", "TLSv1.2");
			props.put("mail.smtp.starttls.required","true");			
		}
		props.put("mail.smtp.port", this.args.getProperty("port")); //TLS Port
		props.put("mail.smtp.auth", "true"); //enable authentication
		ApplicationContext appCtx = new ApplicationContextUtils().getApplicationContext();
		if(appCtx != null) {
			var handler =  appCtx.getBean("internalOperatorService", InternalOperatorUseCase.class);
			this.credentials = handler.getCredentials(this.args.getProperty("credentials"));	
		}
		if(this.credentials == null) {
			throw new DomainException(new Exception("invalid credentials entry in keystore"));
		}
		String userSmtp = this.credentials.getUsername();
		String pwdSmtp = this.credentials.getPassword();
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userSmtp, pwdSmtp);
			}
		};
		Session session = Session.getInstance(props, auth);
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");
	      msg.setFrom(new InternetAddress(this.args.getProperty(FROMMAIL), this.args.getProperty(FROMMAIL)));
	      msg.setReplyTo(InternetAddress.parse(this.args.getProperty(FROMMAIL), false));
	      msg.setSubject(this.args.getProperty("subject"), "UTF-8");
	      StringBuilder body = new StringBuilder(); 
	      if(this.optionals.getProperty("body") != null) {
	    	 body.append(this.optionals.getProperty("body"));
	      }
	      if(this.optionals.getProperty("xcom") != null && !this.optionals.getProperty("xcom").isEmpty()) {
	    	  String xcomname = this.optionals.getProperty("xcom");
	    	  if(!this.xcom.containsKey(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
	    	  }
	    	  DataFrame dfappend = (DataFrame) this.xcom.get(xcomname);
	    	  String appendstr = dfappend.getColumn(0).get(0).toString();
	    	  body.append(appendstr);
	      }
	      msg.setContent(body.toString(),"text/html; charset=UTF-8");
	      msg.setSentDate(new Date());
	      String propname = this.args.getProperty("toEmail");
	      DataFrame xcomdf = (this.xcom.containsKey(propname))?this.xcom.get(propname):null;
	      //DataFrame xcomdf = (DataFrame) this.xcom.get(propname);
	      String mailcalc = "";
	      if(xcomdf != null) {
	    	  mailcalc = xcomdf.getColumn(0).get(0).toString();
	      }
	      String toEmailString = (this.args.getProperty("toEmail").contains("@"))?this.args.getProperty("toEmail"):mailcalc;
	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailString, false));
	      
	      if(this.optionals.getProperty("ccList") != null && !this.optionals.getProperty("ccList").isEmpty()) {
	    	  String [] arrcc = this.optionals.getProperty("ccList").split(";");
	    	  for (int i = 0; i < arrcc.length; i++) {
				String string = arrcc[i];
				msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(string, false));
	    	  }
	    	    
	      }
	      if(this.optionals.getProperty("attachedFilename") != null && !this.optionals.getProperty("attachedFilename").isEmpty()) {
	    	  String attachedFilename = this.optionals.getProperty("attachedFilename");
	    	  String stepAttachedFilename = this.optionals.getProperty("stepAttachedFilename"); 
	    	  var df = (DataFrame) this.xcom.get(stepAttachedFilename);
	    	  //List<Map<String,Object>> lista = df.row(0);
	    	  
	    	  //Map<String,Object> obj = lista.get(0);
	    	  RowProxy obj = df.iterator().next();
	    	  String base64File = (String) obj.get("output");
	    	  if (base64File  != null && !base64File.isEmpty()) {
	    	        byte[] fileBytes = Base64.getDecoder().decode(base64File);
	    	        // Crear una parte del cuerpo del mensaje para el archivo adjunto
	                BodyPart fileBodyPart = new MimeBodyPart();
	                fileBodyPart.setContent(fileBytes, "application/octet-stream");
	                fileBodyPart.setFileName(attachedFilename);

	                // Crear el cuerpo del mensaje y adjuntar la parte del archivo
	                Multipart multipart = new MimeMultipart();
	                multipart.addBodyPart(fileBodyPart);

	                // Establecer el contenido del mensaje como multipart
	                msg.setContent(multipart);
	    	  }
	      }
	      Transport.send(msg);
    	  log.debug(this.getClass()+" end "+this.name);
    	  return DataFrameUtils.createStatusFrame("ok");
	    } catch (Exception e) {
	      log.error(e);
	      throw new DomainException(e);
	    }
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.MailOperator");
		metadata.setType("EXTERNAL");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("credentials", "credentials");
		
		//metadata.setParameter("userSmtp", "text");
		//metadata.setParameter("pwdSmtp", "password");
		metadata.setParameter(FROMMAIL, "text");
		metadata.setParameter("toEmail", "text");
		metadata.setParameter("subject", "text");
		metadata.setParameter("protocol", "list", List.of("plaintext", "TLSv1.2"));
		metadata.setOpts("xcom", "xcom");
		metadata.setOpts("body", "sourcecode");
		metadata.setOpts("attachedFilename", "text");
		metadata.setOpts("stepAttachedFilename", "xcom");
		metadata.setOpts("ccList", "text");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "mail.png";
	}
	
}
