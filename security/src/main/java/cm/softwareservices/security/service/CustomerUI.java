package cm.softwareservices.security.service;

import javax.mail.MessagingException;

public interface CustomerUI {

    boolean requestPasswordReset(String email) throws MessagingException;

    boolean resetPassword(String token, String password);
}
