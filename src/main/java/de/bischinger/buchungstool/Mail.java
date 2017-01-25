package de.bischinger.buchungstool;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;

import static javax.mail.Message.RecipientType.TO;

@Stateless
public class Mail
{
	@Resource(name = "java:jboss/mail/postfix")
	private Session mailSession;

	//TODO
	public void send()
	{

		try
		{
			MimeMessage m = new MimeMessage(mailSession);
			Address from = new InternetAddress("ab@siteos.de");
			Address[] to = new InternetAddress[] { new InternetAddress("ab@siteos.de") };

			m.setFrom(from);
			m.setRecipients(TO, to);
			m.setSubject("registration");
			m.setSentDate(new Date());
			m.setContent("Mail sent from JBoss AS 7", "text/plain");
			Transport.send(m);
			System.out.println("Mail sent!");
		}
		catch(MessagingException e)
		{
			e.printStackTrace();
		}
	}
}