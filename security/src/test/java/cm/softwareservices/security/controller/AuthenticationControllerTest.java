package cm.softwareservices.security.controller;

import cm.softwareservices.security.request.AuthenticationRequest;
import cm.softwareservices.security.request.RegisterRequest;
import cm.softwareservices.security.response.AuthenticationResponse;
import cm.softwareservices.security.serviceImpl.AuthenticationService;
import cm.softwareservices.security.serviceImpl.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService mockAuthenticationService;
    @MockBean
    private CustomerService mockService;

    @Test
    void testRegister() throws Exception {
        // Setup
        when(mockAuthenticationService.register(
                new RegisterRequest("firstname", "lastname", "email", "password")))
                .thenReturn(new AuthenticationResponse("token"));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/ss/auth/register")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testAuthentication() throws Exception {
        // Setup
        when(mockAuthenticationService.authentication(new AuthenticationRequest("email", "password")))
                .thenReturn(new AuthenticationResponse("token"));

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/ss/auth/authenticate")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testResetPasswordRequest() throws Exception {
        // Setup
        when(mockService.requestPasswordReset("email")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/ss/auth/password-reset-request")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testResetPasswordRequest_CustomerServiceThrowsMessagingException() throws Exception {
        // Setup
        when(mockService.requestPasswordReset("email")).thenThrow(MessagingException.class);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/ss/auth/password-reset-request")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }

    @Test
    void testResetPassword() throws Exception {
        // Setup
        when(mockService.resetPassword("token", "password")).thenReturn(false);

        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("/ss/auth/password-reset")
                        .content("content").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
    }
}
