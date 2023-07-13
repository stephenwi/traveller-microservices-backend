package cm.softwareservices.security.service;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface EmailUI {

    void resetPasswordEmail(String email, String token) throws MessagingException;
}
