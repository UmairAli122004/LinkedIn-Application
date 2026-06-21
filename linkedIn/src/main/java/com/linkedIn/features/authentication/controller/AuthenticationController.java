package com.linkedIn.features.authentication.controller;

import com.linkedIn.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.features.authentication.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RequiredArgsConstructor
@RequestMapping("/api/v1/authentication")
@RestController
@Validated
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /*
    *
    * @RequestAttribute("authenticatedUser") is used to read an attribute
    *  that was already added to the current HTTP request by some earlier
    * component, usually a Filter, Interceptor, or another controller.
    *
    *
    * authenticatedUser is not coming from the client request body, path variable, or query parameter.
    * Instead, Spring looks inside the HttpServletRequest and retrieves an attribute named "authenticatedUser".
    *
    * */
    @GetMapping("/user")
    public AuthenticationUser getUser(@RequestAttribute("authenticatedUser") AuthenticationUser authenticationUser){
        return authenticationService.getUser(authenticationUser.getEmail());
    }

    @PostMapping("/login")
    public AuthenticationResponseBody login(@Valid @RequestBody AuthenticationRequestBody registerRequestBody){
        return authenticationService.login(registerRequestBody);
    }

    @PostMapping("/register")
    public AuthenticationResponseBody register(@Valid @RequestBody AuthenticationRequestBody loginRequestBody) throws MessagingException, UnsupportedEncodingException {
        return authenticationService.register(loginRequestBody);
    }


    @PutMapping("/validate-email-verification-token")
    public String verifyEmail(@RequestParam String token,
                              @RequestAttribute("authenticatedUser") AuthenticationUser user) {
        authenticationService.validateEmailVerificationToken(token, user.getEmail());
        return "Email verified successfully.";
    }


    @GetMapping("/send-email-verification-token")
    public String sendEmailVerificationToken(@RequestAttribute("authenticatedUser") AuthenticationUser user){
        authenticationService.sendEmailVerificationToken(user.getEmail());
        return "Email Verification token sent successfully.";
    }

    @PutMapping("/send-password-reset-token")
    public String sendPasswordResetToken(@RequestParam String email){
        authenticationService.sendPasswordResetToken(email);
        return "Password reset token sent successfully.";
    }

    @PutMapping("/reset-password")
    public String resetPassword(@RequestParam String newPassword, @RequestParam String token,
                                  @RequestParam String email) {

        authenticationService.resetPassword(email, newPassword, token);

        return "Password reset successfully.";
    }

}
