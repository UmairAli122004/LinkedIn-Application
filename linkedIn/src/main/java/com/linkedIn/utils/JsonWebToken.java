package com.linkedIn.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.util.Date;

@Configuration
public class JsonWebToken {
    private final String SECRET = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890ackedABCDEFGH";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String email){
        long EXPIRATION_TIME = 1000 * 60 * 60;
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }


    public String extractEmailFromToken(String token){
        return extractClaims(token).getSubject();
    }

    public Claims extractClaims(String token){
        return  Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
//    public boolean validateToken(String username, UserDetails userDetails, String token) {
//        return username.equals(userDetails.getUsername())  && !isTokenExpired(token);
//    }
    public boolean isTokenExpired(String token) {
        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }
}
