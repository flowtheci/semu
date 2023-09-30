package com.semu.api.controller;

import com.semu.api.model.AuthDTO;
import com.semu.api.model.User;
import com.semu.api.service.JwtService;
import com.semu.api.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import java.util.Date;

@RestController
@CrossOrigin(origins = "https://semu.vercel.app/")
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/register")
    public ResponseEntity<AuthDTO> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        System.out.println(Base64.encodeBase64String(jwtSecret.getBytes()));
        String token = Jwts.builder()
                .setSubject(registeredUser.getEmail())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 1 day expiration
                .signWith(SignatureAlgorithm.HS512, Base64.encodeBase64String(jwtSecret.getBytes()))
                .compact();
        return ResponseEntity.ok(userService.wrapJwtToken(token));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthDTO> authenticateUser(@RequestParam String email, @RequestParam String password) {
        User user = userService.authenticateUser(email, password);
        if (user != null) {
            String token = Jwts.builder()
                    .setSubject(email)
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
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }


}
