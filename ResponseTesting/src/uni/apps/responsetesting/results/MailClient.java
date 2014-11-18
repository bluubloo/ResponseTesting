package uni.apps.responsetesting.results;

import java.util.Calendar;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailClient extends Authenticator {

	private String user;
	private String pass;
	
	private String[] to;
	private String from;
	
	private String port;
	private String sport;
	
	private String host;
	
	private String subject;
	private String body;
	
	private boolean auth;
	private boolean debuggable;
	
	private Multipart multipart;
	
	public MailClient(){
		host = "smtp.gmail.com";
		port = "465";
		sport = "465";
		
		user = "";
		pass = "";
		from = "";
		subject = "";
		body = "";
		
		debuggable = false;
		auth = true;
		
		multipart = new MimeMultipart();
		
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);	
	}
	
	public MailClient(String user, String pass){
		this();
		
		this.user = user;
		this.pass = pass;
	}
	
	public boolean send() throws Exception {
		Properties prop = setProperties();
		if(!user.equals("") && !pass.equals("") && to.length > 0 && !from.equals("") && 
				!subject.equals("") && !body.equals("")){
			Session session = Session.getInstance(prop, this);
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(from));
			InternetAddress[] addressTo = new InternetAddress[to.length];
			for(int i = 0; i < to.length; i++){
				addressTo[i] = new InternetAddress(to[i]);
			}
			msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
			
			msg.setSubject(subject);
			msg.setSentDate(Calendar.getInstance().getTime());
			
			BodyPart messageBody = new MimeBodyPart();
			messageBody.setText(body);
			multipart.addBodyPart(messageBody);
			
			msg.setContent(multipart);
			
			Transport.send(msg);
			return true;			
		} else{
			return false;
		}
	}
	
	public void addAttachment(String fileName) throws Exception {
		BodyPart messageBodyPart = new MimeBodyPart();
		DataSource source = new FileDataSource(fileName);
		messageBodyPart.setDataHandler(new DataHandler(source));
		messageBodyPart.setFileName(fileName);
		multipart.addBodyPart(messageBodyPart);
	}
	
	@Override
	public PasswordAuthentication getPasswordAuthentication(){
		return new PasswordAuthentication(user, pass);
	}

	private Properties setProperties() {
		Properties prop = new Properties();
		
		prop.put("mail.smtp.host", host);
		
		if(debuggable)
			prop.put("mail.debug", "true");
		
		if(auth)
			prop.put("mail.smtp.auth", "true");
		
		prop.put("mail.smtp.port", port);
		prop.put("mail.smtp.socketFactory.port", sport);
		prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		prop.put("mail.smtp.socketFactory.fallback", "false");
		
		return prop;
	}
	
	//---------------------------------------------------------------------------------------
	//GETTERS AND SETTERS
	
	public String getBody(){
		return body;
	}
	
	public void setBody(String s){
		body = s;
	}
	
	public String getSubject(){
		return subject;
	}
	
	public void setSubject(String s){
		subject = s;
	}
	
	public String getFrom(){
		return from;
	}
	
	public void setFrom(String s){
		from = s;
	}
	
	public String[] getTo(){
		return to;
	}
	
	public void setTo(String[] s){
		to = s;
	}
}
