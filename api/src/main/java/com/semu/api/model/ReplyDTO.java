package com.semu.api.model;

public class ReplyDTO {

    private Long id;
    private String lastMessage;
    private String lastMessageTimestamp;

    public ReplyDTO() {
    }

    public ReplyDTO(Long id, String lastMessage, String lastMessageTimestamp) {
        this.id = id;
        this.lastMessage = lastMessage;
        this.lastMessageTimestamp = lastMessageTimestamp;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setLastMessageTimestamp(String lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }
}
