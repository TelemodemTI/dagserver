package main.infra.adapters.operators;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Callable;
import org.json.JSONObject;
import main.domain.annotations.Operator;
import main.domain.core.MetadataManager;
import main.domain.core.OperatorStage;
import main.domain.exceptions.DomainException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


@Operator(args={"host","port","userSmtp","pwdSmtp","fromMail","toEmail","subject"},optionalv = {"body","xcom"})
public class MailOperator extends OperatorStage implements Callable<String> {

	private static final String FROMMAIL = "fromMail";
	
	@Override
	public String call() throws DomainException {		
		log.debug(this.getClass()+" init "+this.name);
		log.debug("args");
		log.debug(this.args);
		
		Properties props = new Properties();
		props.put("mail.smtp.host", this.args.getProperty("host")); //SMTP Host
		props.put("mail.smtp.ssl.trust", this.args.getProperty("host"));
		props.put("mail.smtp.starttls.enable", "true");
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
	      if(this.optionals.getProperty("xcom") != null) {
	    	  String xcomname = this.args.getProperty("xcom");
	    	  if(!this.xcom.has(xcomname)) {
					throw new DomainException("xcom not exist for dagname::"+xcomname);
	    	  }
	    	  String appendstr = this.xcom.get(xcomname).toString();
	    	  body.append(appendstr);
	      }
	      msg.setContent(body.toString(),"text/html; charset=UTF-8");
	      msg.setSentDate(new Date());
	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.args.getProperty("toEmail"), false));
    	  Transport.send(msg);
    	  log.debug(this.getClass()+" end "+this.name);
    	  return "ok";
	    } catch (Exception e) {
	      log.error(e);
	      return e.getMessage();
	    }
	}
	
	
	@Override
	public JSONObject getMetadataOperator() {
		MetadataManager metadata = new MetadataManager("main.infra.adapters.operators.MailOperator");
		metadata.setParameter("host", "text");
		metadata.setParameter("port", "number");
		metadata.setParameter("userSmtp", "text");
		metadata.setParameter("pwdSmtp", "password");
		metadata.setParameter(FROMMAIL, "text");
		metadata.setParameter("toEmail", "text");
		metadata.setParameter("subject", "text");
		metadata.setOpts("xcom", "text");
		metadata.setOpts("body", "sourcecode");
		return metadata.generate();
	}
	@Override
	public String getIconImage() {
		return "mail.png";
	}
	
}
