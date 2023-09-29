package com.semu.api.service;

import com.semu.api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Autowired
    private UserService userService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String validateTokenAndGetSubject(String token) {
        try {
            // Parse the JWT token
            Jws<Claims> jws = Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes()) // Set the secret key for signature validation
                    .parseClaimsJws(token); // Parse the token

            // Get the subject (usually the user identifier) from the token
            String email = jws.getBody().getSubject();
            User user = userService.getUserByEmail(email);

            if (user == null) {
                System.err.println("JWT token was valid but subject " + email + " was not found in the database");
                return null;
            }

            return email;

        } catch (Exception e) {
            // Log the exception and return null if token validation fails
            System.err.println("Invalid JWT token: " + e.getMessage());
            return null;
        }
    }
}
