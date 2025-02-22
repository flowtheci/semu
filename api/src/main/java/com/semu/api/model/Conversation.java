package com.semu.api.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversations")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private String threadId;

    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("timestamp ASC")
    private List<Message> messages = new ArrayList<>();

    private LocalDateTime lastUpdated;

    public void addMessage(Message message) {
        messages.add(message);
        message.setConversation(this);
        lastUpdated = LocalDateTime.now();
    }

    public void removeMessage(Message message) {
        messages.remove(message);
        message.setConversation(null);
        lastUpdated = LocalDateTime.now();
    }

    public Conversation() {
    }

    public Conversation(Long id, User user, List<Message> messages, LocalDateTime lastUpdated) {
        this.id = id;
        this.user = user;
        this.messages = messages;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public String getTitle() {
        return title;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Message getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public Message getLastUserMessage() {
        Message lastMessage = messages.get(messages.size() - 1);
        return lastMessage.isUser() ? lastMessage : messages.get(messages.size() - 2);
    }

    public Message getLastAssistantMessage() {
        Message lastMessage = messages.get(messages.size() - 1);
        return !lastMessage.isUser() ? lastMessage : messages.get(messages.size() - 2);
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", user=" + user +
                ", messages=" + messages +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
