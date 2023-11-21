package com.semu.api.service;

import com.semu.api.model.AuthDTO;
import com.semu.api.model.User;
import com.semu.api.repository.UserRepository;
import jnr.constants.platform.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;


    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registerUser(User user) {
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

    public boolean userCanSendMessage(String userEmail) {
        User user = getUserByEmail(userEmail);
        if (user.isAdministrator()) return true; //admin saab alati saata
        if (user.getResetTime() == null || LocalDateTime.now().isAfter(user.getResetTime())) { // aeg on möödas, reset kasutaja limiidile
            user.setMessageCount(1);
            user.setResetTime(LocalDateTime.now().plusHours(3));
            userRepository.save(user);
            return true;
        } else {
            if (user.getMessageCount() >= 15) {
                return false;
            } else {
                user.setMessageCount(user.getMessageCount() + 1);
                userRepository.save(user);
                return true;
            }
        }
    }

    public LocalDateTime getUserResetTime(String userEmail) {
        User user = getUserByEmail(userEmail);
        return user.getResetTime();
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
