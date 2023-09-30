package com.semu.api.model;

public class AuthDTO {
    private String timestamp = "";
    private String token = "";
    private boolean isOk = false;

    public AuthDTO() {
    }

    public AuthDTO(String timestamp, String token) {
        this.timestamp = timestamp;
        this.token = token;
        this.isOk = true;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getToken() {
        return token;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
