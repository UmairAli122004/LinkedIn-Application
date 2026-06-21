package com.linkedIn.features.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class AuthenticationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    private Boolean emailVerified=false;
    private String emailVerificationToken=null;
    private LocalDateTime emailVerificationTokenExpiryDate =null;

    @JsonIgnore //Do not show password in JSON Response
    @Column(nullable = false)
    private String password;
    private String passwordResetToken = null;
    private LocalDateTime passwordResetTokenExpiryDate =null;

    public AuthenticationUser(String email, String password){
        this.email = email;
        this.password=password;
    }
}