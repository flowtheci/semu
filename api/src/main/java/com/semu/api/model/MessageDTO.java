package com.semu.api.model;

public class MessageDTO {

    private Long id;
    private String message;
    private String timestamp;
    private boolean isUser;

    public MessageDTO() {
    }

    public MessageDTO(Long id, String message, String timestamp, boolean isUser) {
        this.id = id;
        this.message = message;
        this.timestamp = timestamp;
        this.isUser = isUser;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(boolean user) {
        isUser = user;
    }
}
