package cm.softwareservices.security.controller;

import cm.softwareservices.security.dto.OperationStatusDto;
import cm.softwareservices.security.dto.PasswordResetDto;
import cm.softwareservices.security.dto.PasswordResetRequest;
import cm.softwareservices.security.enun.RequestOperationName;
import cm.softwareservices.security.enun.RequestOperationStatus;
import cm.softwareservices.security.request.AuthenticationRequest;
import cm.softwareservices.security.request.RegisterRequest;
import cm.softwareservices.security.response.AuthenticationResponse;
import cm.softwareservices.security.serviceImpl.AuthenticationService;
import cm.softwareservices.security.serviceImpl.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/ss/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final CustomerService service;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authenticationService.register(registerRequest));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest authenticationRequest) {
          return  ResponseEntity.ok(authenticationService.authentication(authenticationRequest));
    }

    @PostMapping("/password-reset-request")
    public OperationStatusDto resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest) throws MessagingException {
        OperationStatusDto operationStatusDto = new OperationStatusDto();
        boolean operationResult = service.requestPasswordReset(passwordResetRequest.getEmail());
        operationStatusDto.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        operationStatusDto.setOperationResult(RequestOperationStatus.ERROR.name());

        if (operationResult) {
            operationStatusDto.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }
        return operationStatusDto;
    }

    @PostMapping("/password-reset")
    public OperationStatusDto resetPassword(@RequestBody PasswordResetDto passwordResetDto) {
        OperationStatusDto operationStatusDto = new OperationStatusDto();
        boolean operationResult = service.resetPassword(passwordResetDto.getToken(), passwordResetDto.getPassword());
        operationStatusDto.setOperationResult(RequestOperationStatus.ERROR.name());
        operationStatusDto.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        if (operationResult) {
            operationStatusDto.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return operationStatusDto;
    }
}
