package com.semu.api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String passwordHash;
    private int messageCount;
    private LocalDateTime resetTime;
    private boolean administrator;

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public User(String firstName, String lastName, String email, String passwordHash) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
        this.messageCount = 0;
        this.administrator = false;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public LocalDateTime getResetTime() {
        return resetTime;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public void setResetTime(LocalDateTime resetTime) {
        this.resetTime = resetTime;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

}

