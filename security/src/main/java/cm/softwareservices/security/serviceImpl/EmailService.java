package cm.softwareservices.security.serviceImpl;

import cm.softwareservices.security.service.EmailUI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

@Service
public class EmailService implements EmailUI {

    @Value("${spring.mail.username}")
    String username;
    @Value("${spring.mail.password}")
    String password;
    @Value("${isc.homepage}")
    String clientHomeUrl;

    private final String PASSWORD_RESET_SUBJECT = "Password reset request";
    private String PASSWORD_RESET_BODY = "<h1>A password reset request has been sent from you</h1>" +
            "<p>Hello, " +
            "Someone has requested a new password for your account. If that's your action, please click the link below to get a new password." +
            "<br/><a href='$clientPage/verification-service/password-reset?token=$tokenValue'>Click come in</a><br/><br/>" +
            "Thank.";
    @Override
    public void resetPasswordEmail(String email, String token) throws MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.allow8bitmime", "true");
        properties.setProperty("mail.smtps.allow8bitmime", "true");
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username, false));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(email + " - " + PASSWORD_RESET_SUBJECT);
        String htmlBodyWithToken = PASSWORD_RESET_BODY.replace("$tokenValue", token).replace("$clientPage", clientHomeUrl);
        message.setContent(htmlBodyWithToken, "text/html");
        message.setSentDate(new Date());

        Transport.send(message);
    }
}
