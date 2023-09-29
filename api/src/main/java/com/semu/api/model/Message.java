package com.semu.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private LocalDateTime timestamp;
    private boolean isUser;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    public Message(Long id, String content, LocalDateTime timestamp, boolean isUser, Conversation conversation) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
        this.isUser = isUser;
        this.conversation = conversation;
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isUser() {
        return isUser;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setIsUser(boolean user) {
        isUser = user;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                ", isUser=" + isUser +
                ", conversation=" + conversation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message)) return false;
        return id.equals(message.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
