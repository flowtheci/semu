package com.semu.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;
    private LocalDateTime timestamp;
    private boolean isUser;

    private String threadMessageId;

    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;

    public Message(String content, LocalDateTime timestamp, boolean isUser, Conversation conversation) {
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

    public String getThreadMessageId() {
        return threadMessageId;
    }

    public void setThreadMessageId(String threadMessageId) {
        this.threadMessageId = threadMessageId;
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
                ", conversationId=" + conversation.getId() +
                '}';
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
