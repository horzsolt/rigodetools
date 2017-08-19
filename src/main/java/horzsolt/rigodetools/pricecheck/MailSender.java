package horzsolt.rigodetools.pricecheck;

import horzsolt.rigodetools.RigodetoolsApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
public class MailSender {

    @Autowired
    private RigodetoolsApplication.GMailAccount gMailAccount;

    public void sendMail(String text) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(gMailAccount.getUsername(), gMailAccount.getPassword());
                    }
                });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("horzsolt2006@gmail.com"));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("horzsolt2006@gmail.com"));
            message.setSubject("Price drop alert notification");
            message.setText(text);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
