package com.example.demoauth.configs;


import com.example.demoauth.services.UserDetailsImpl;
import com.example.demoauth.services.UserDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.security.sasl.AuthenticationException;
import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        Date dateNow = new Date();

        return Jwts.builder()
                .subject(userPrincipal.getEmail())
                .issuedAt(dateNow)
                .expiration(new Date(dateNow.getTime() + jwtExpirationMs))
                .signWith(key)
                .compact();
    }

    public boolean validateJwtToken(String jwt) throws AuthenticationException {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(jwt);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new AuthenticationException("Authentication validation failed: " + e.getMessage(), e);
        }
    }

    public String getUsernameFromJwtToken(String jwt) {
        SecretKey secret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getSubject();
    }
}
