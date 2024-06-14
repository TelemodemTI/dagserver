package main.cl.dagserver.infra.adapters.operators;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.json.JSONObject;
import main.cl.dagserver.domain.annotations.Operator;
import main.cl.dagserver.domain.core.Dagmap;
import main.cl.dagserver.domain.core.MetadataManager;
import main.cl.dagserver.domain.core.OperatorStage;
import main.cl.dagserver.domain.exceptions.DomainException;
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


@Operator(args={"host","port","userSmtp","pwdSmtp","fromMail","toEmail","subject","protocol"},optionalv = {"body","xcom","attachedFilename","stepAttachedFilename","ccList"})
public class MailOperator extends OperatorStage {

	private static final String FROMMAIL = "fromMail";
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Dagmap> call() throws DomainException {		
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
		//create Authenticator object to pass in Session.getInstance argument
		var args = this.args;
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(args.getProperty("userSmtp"), args.getProperty("pwdSmtp"));
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
	    	  if(!this.xcom.has(xcomname)) {
					throw new DomainException(new Exception("xcom not exist for dagname::"+xcomname));
	    	  }
	    	  String appendstr = this.xcom.get(xcomname).toString();
	    	  body.append(appendstr);
	      }
	      msg.setContent(body.toString(),"text/html; charset=UTF-8");
	      msg.setSentDate(new Date());
	      
	      String toEmailString = (this.args.getProperty("toEmail").contains("@"))?this.args.getProperty("toEmail"):(String) ((List<Dagmap>) this.xcom.get(this.args.getProperty("toEmail"))).get(0).get("output");;
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
	    	  List<Dagmap> lista = (List<Dagmap>) this.xcom.get(stepAttachedFilename);
	    	  Dagmap obj = lista.get(0);
	    	  String base64File = (String) obj.get("result");
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
    	  return Dagmap.createDagmaps(1, "status", "ok");
	    } catch (Exception e) {
	      log.error(e);
	      return Dagmap.createDagmaps(1, "status", e.getMessage());
	    }
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.cl.dagserver.infra.adapters.operators.MailOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("userSmtp", "text");
		metadata.setParameter("pwdSmtp", "password");
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
