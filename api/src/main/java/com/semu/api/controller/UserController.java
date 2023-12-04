package com.semu.api.controller;

import com.semu.api.model.AuthDTO;
import com.semu.api.model.Keys;
import com.semu.api.model.User;
import com.semu.api.model.UserLoginDTO;
import com.semu.api.service.JwtService;
import com.semu.api.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Objects;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> registerUser(@RequestParam String key, @RequestBody User user) {
        if (!Objects.equals(key, Keys.VILLI.getKey()) && !Objects.equals(key, Keys.HARLI.getKey()) && !Objects.equals(key, Keys.KEVIN.getKey())) {
            AuthDTO response = new AuthDTO();
            response.setToken("Invalid access key");
            return ResponseEntity.status(401).body(response);
        }

        User registeredUser = userService.registerUser(user);
        String token = Jwts.builder()
                .setSubject(registeredUser.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 1 day expiration
                .signWith(SignatureAlgorithm.HS512, Base64.encodeBase64String(jwtSecret.getBytes()))
                .compact();
        return ResponseEntity.ok(userService.wrapJwtToken(token));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthDTO> authenticateUser(@RequestBody UserLoginDTO info) {
        User user = userService.authenticateUser(info.getEmail(), info.getPasswordHash());
        if (user != null) {
            String token = Jwts.builder()
                    .setSubject(info.getEmail())
                    .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 1 day expiration
                    .signWith(SignatureAlgorithm.HS512, Base64.encodeBase64String(jwtSecret.getBytes()))
                    .compact();
            return ResponseEntity.ok(userService.wrapJwtToken(token));
        } else {
            return ResponseEntity.status(401).body(userService.invalidToken());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers(@RequestHeader(name = "Authorization") String authToken) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null || !email.equals("admin")) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }
}
