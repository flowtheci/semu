package com.semu.api.model;

import java.sql.Blob;

public class ReplyDTO {

    private Long id;
    private String title;
    private String lastMessage;
    private String lastMessageTimestamp;
    private byte[] audio;

    public ReplyDTO() {
    }

    public ReplyDTO(Long id, String lastMessage, String lastMessageTimestamp) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public ReplyDTO(Long id, String lastMessage, String lastMessageTimestamp, byte[] audio) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.audio = audio;
    }


    public Long getId() {
        return id;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public String getTitle() {
        return title;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
