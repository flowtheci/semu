package com.semu.api.service;

import com.semu.api.model.AuthDTO;
import com.semu.api.model.User;
import com.semu.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
        // Hash the user's password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        return userRepository.save(user);
    }

    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPasswordHash())) {
            return user;
        }
        return null;
    }

    public AuthDTO wrapJwtToken(String token) {
        return new AuthDTO(String.valueOf(System.currentTimeMillis()), token);
    }

    public AuthDTO invalidToken() {
        return new AuthDTO();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
