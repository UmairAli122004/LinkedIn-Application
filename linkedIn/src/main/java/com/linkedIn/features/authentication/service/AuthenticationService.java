package com.linkedIn.features.authentication.service;

import com.linkedIn.features.authentication.dto.AuthenticationRequestBody;
import com.linkedIn.features.authentication.dto.AuthenticationResponseBody;
import com.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.utils.Encoder;
import com.linkedIn.utils.JsonWebToken;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@EnableCaching
public class AuthenticationService {
    private final AuthenticationUserRepository authenticationUserRepository;
    private final Encoder encoder;
    private final JsonWebToken jwt;

    @Cacheable(value = "users", key = "#email")
    public AuthenticationUser getUser(String email) {
        return authenticationUserRepository.findByEmail(email).orElseThrow(()-> new IllegalArgumentException("User not found"));
    }


    public AuthenticationResponseBody register(AuthenticationRequestBody registerRequestBody) {
        authenticationUserRepository.save(
                new AuthenticationUser(
                        registerRequestBody.getEmail(),
                        encoder.encode(registerRequestBody.getPassword())
                )
        );
        return new AuthenticationResponseBody("token", "User Register Successfully");
    }

    @Cacheable(value = "users", key = "#loginRequestBody.email")
    public AuthenticationResponseBody login(AuthenticationRequestBody loginRequestBody) {
        AuthenticationUser user = authenticationUserRepository.findByEmail(loginRequestBody.getEmail())
                .orElseThrow(()-> new IllegalArgumentException("User not found"));
        if(!encoder.matchers(loginRequestBody.getPassword(), user.getPassword())){
            throw new IllegalArgumentException("Incorrect Password");
        }
        String token = jwt.generateToken(loginRequestBody.getEmail());
        return new AuthenticationResponseBody(token, "Authentication Succeed");
    }
}
