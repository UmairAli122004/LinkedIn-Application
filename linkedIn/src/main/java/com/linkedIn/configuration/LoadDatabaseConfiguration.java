package com.linkedIn.configuration;

import com.linkedIn.features.authentication.model.AuthenticationUser;
import com.linkedIn.features.authentication.repository.AuthenticationUserRepository;
import com.linkedIn.utils.Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@RequiredArgsConstructor
@Configuration
public class LoadDatabaseConfiguration {
    private final Encoder encoder;
    @Bean
    public CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository){
        return args -> {
            AuthenticationUser authenticationUser = new AuthenticationUser("aliumair7379@gmail.com", encoder.encode("umair@9876"));
            authenticationUserRepository.save(authenticationUser);
        };
    }
}
