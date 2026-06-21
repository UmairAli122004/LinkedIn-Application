package com.linkedIn.features.authentication.service;

import com.linkedIn.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.utils.EmailService;
import com.linkedIn.utils.Encoder;
import com.linkedIn.utils.JsonWebToken;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@EnableCaching
public class AuthenticationService {
    private final AuthenticationUserRepository authenticationUserRepository;
    private final Encoder encoder;
    private final JsonWebToken jwt;
    private final EmailService emailService;
    private final int durationInMinutes = 10;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);


    public static String generateEmailVerificationToken(){
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder();
        for(int i=0; i<5; i++){
            token.append(random.nextInt(10)); //0-9
        }
        return token.toString();
    }

    public void sendEmailVerificationToken(String email){
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if(user.isPresent() && !user.get().getEmailVerified()){
            String emailVerificationToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(emailVerificationToken);

            user.get().setEmailVerificationToken(hashedToken);
            user.get().setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

            authenticationUserRepository.save(user.get());

            String subject = "Email Verification";

            String body = String.format("""
                    <p>Only one step to take full advantage of LinkedIn.</p>
                
                    <p>Your OTP: <b>%s</b></p>
                
                    <p>The code will expire in %s minutes.</p>
                    """, emailVerificationToken, durationInMinutes);

            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        } else {
             throw new IllegalArgumentException("Email verification token failed, or email is already verified.");
        }
    }


   public void validateEmailVerificationToken(String token, String email){
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if(user.isPresent() && encoder.matchers(token, user.get().getEmailVerificationToken())
                && !user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())){

            user.get().setEmailVerified(true);
            user.get().setEmailVerificationToken(null);
            user.get().setEmailVerificationTokenExpiryDate(null);

            authenticationUserRepository.save(user.get());

        } else if (user.isPresent() && encoder.matchers(token, user.get().getEmailVerificationToken())
                && user.get().getEmailVerificationTokenExpiryDate().isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException("Email verification token expired.");
            
        }else{
            throw new IllegalArgumentException("Email verification token Failed.");
        }
   }


    public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) throws MessagingException, UnsupportedEncodingException {
        Optional<AuthenticationUser> authUser = authenticationUserRepository.findByEmail(registerRequestBody.getEmail());

        if(authUser.isPresent()){
            throw new IllegalArgumentException("Duplicate Email-ID");
        }

//        AuthenticationUser user = authenticationUserRepository.save(
//                new AuthenticationUser(
//                        registerRequestBody.getEmail(),
//                        encoder.encode(registerRequestBody.getPassword())
//                )
//        );

        AuthenticationUser user = new AuthenticationUser();

        String emailVerificationToken = generateEmailVerificationToken();
        String hashedToken = encoder.encode(emailVerificationToken);

        user.setEmailVerificationToken(hashedToken);
        user.setEmailVerificationTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));
        user.setEmail(registerRequestBody.getEmail());
        user.setPassword(encoder.encode(registerRequestBody.getPassword()));

        authenticationUserRepository.save(user);

        String subject = "Email Verification";

        String body = String.format("""
                    <p>Only one step to take full advantage of LinkedIn.</p>
                
                    <p>Your OTP: <b>%s</b></p>
                
                    <p>The code will expire in %s minutes.</p>
                    """, emailVerificationToken, durationInMinutes);

        try {
            emailService.sendEmail(registerRequestBody.getEmail(), subject, body);
        } catch (Exception e) {
            logger.info("Error while sending email: {}", e.getMessage());
        }

        //JWT is generated so the frontend can store it and send it back with future requests.
        String token = jwt.generateToken(registerRequestBody.getEmail());
        return new AuthenticationResponseBody(token, "User Register Successfully");
    }

    public void resetPassword(String email, String newPassword, String token){
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent() && encoder.matchers(token, user.get().getPasswordResetToken())
                && !user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {

            user.get().setPasswordResetToken(null);
            user.get().setPasswordResetTokenExpiryDate(null);
            user.get().setPassword(encoder.encode(newPassword));

            authenticationUserRepository.save(user.get());

        } else if (user.isPresent() && encoder.matchers(token, user.get().getPasswordResetToken())
                && user.get().getPasswordResetTokenExpiryDate().isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException("Password reset token expired.");

        } else {
            throw new IllegalArgumentException("Password reset token failed.");
        }
    }


    public void sendPasswordResetToken(String email) {
        Optional<AuthenticationUser> user = authenticationUserRepository.findByEmail(email);
        if (user.isPresent()) {
            String passwordResetToken = generateEmailVerificationToken();
            String hashedToken = encoder.encode(passwordResetToken);

            user.get().setPasswordResetToken(hashedToken);
            user.get().setPasswordResetTokenExpiryDate(LocalDateTime.now().plusMinutes(durationInMinutes));

            authenticationUserRepository.save(user.get());

            String subject = "Password Reset";
            String body = String.format("""
                    <p>Only one step to take full advantage of LinkedIn.</p>
                
                    <p>Your OTP: <b>%s</b></p>
                
                    <p>The code will expire in %s minutes.</p>
                    """, passwordResetToken, durationInMinutes);

//            String body = String.format("""
//                    You requested a password reset.
//
//                    Enter this code to reset your password: %s. The code will expire in %s minutes.""",
//                    passwordResetToken, durationInMinutes);

            try {
                emailService.sendEmail(email, subject, body);
            } catch (Exception e) {
                logger.info("Error while sending email: {}", e.getMessage());
            }
        }else {
            throw new IllegalArgumentException("User not found.");
        }
    }


    @Cacheable(value = "users", key = "#email")
    public AuthenticationUser getUser(String email) {
        return authenticationUserRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("User not found"));
    }

    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticationUserRepository.findByEmail(loginRequestBody.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));

        if(!encoder.matchers(loginRequestBody.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect Password");
        }

        String token = jwt.generateToken(loginRequestBody.getEmail());
        return new AuthenticationResponseBody(token, "Authentication Succeed");
    }

}
