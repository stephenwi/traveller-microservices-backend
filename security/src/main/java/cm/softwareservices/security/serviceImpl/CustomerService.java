package cm.softwareservices.security.serviceImpl;


import cm.softwareservices.security.entity.Customer;
import cm.softwareservices.security.entity.ResetPasswordToken;
import cm.softwareservices.security.jwt.JwtService;
import cm.softwareservices.security.service.CustomerRepository;
import cm.softwareservices.security.service.CustomerUI;
import cm.softwareservices.security.service.ResetPasswordTokenRepository;
import cm.softwareservices.security.utils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CustomerService implements CustomerUI {


    private PasswordEncoder encoder;
    private ResetPasswordTokenRepository tokenRepository;
    private EmailService emailService;
    private CustomerRepository customerRepository;
    private JwtService jwtService;

    @Override
    public boolean requestPasswordReset(String email) throws MessagingException {

        Optional<Customer> customer = customerRepository.findCustomerByEmail(email);
        if (!customer.isPresent()) {
            return false;
        }
        String token = generateResetPasswordToken(customer.get().getId());

        ResetPasswordToken resetPasswordToken = new ResetPasswordToken();
        resetPasswordToken.setToken(token);
        resetPasswordToken.setCustomer(customer.get());

        // save Reset Password token
        tokenRepository.save(resetPasswordToken);
        emailService.resetPasswordEmail(email, token);

        return true;
    }

    @Override
    public boolean resetPassword(String token, String password) {
        boolean returnValue = false;
        if (hasTokenExeired(token)) {
            return returnValue;
        }

        ResetPasswordToken resetPasswordToken = tokenRepository.findByToken(token);
        if (resetPasswordToken == null) {
            return  returnValue;
        }

        // Prepare a new customer password
        String passwordEncoder = encoder.encode(password);

        // Update customer password and persist in DB
        Customer customer = resetPasswordToken.getCustomer();
        customer.setPassword(passwordEncoder);
        Customer customerSaved = customerRepository.save(customer);

        if (customerSaved != null && customerSaved.getPassword().equalsIgnoreCase(passwordEncoder)) {
            returnValue = true;
        }
        tokenRepository.delete(resetPasswordToken);
        return returnValue;
    }

    public String generateResetPasswordToken(int userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setExpiration(new Date((new Date()).getTime() + Constants.ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
                .signWith(SignatureAlgorithm.HS512, Constants.SIGNING_KEY)
                .compact();
    }

    public boolean hasTokenExeired(String token) {
        Claims claims = Jwts.parser().setSigningKey(Constants.SIGNING_KEY).parseClaimsJws(token).getBody();
        Date tokenExpirationDate = claims.getExpiration();
        Date today = new Date();
        return tokenExpirationDate.before(today);
    }
}
