package com.semu.api.model;

public class TranscriptionDTO {

    private Long conversationId;
    private String title;
    private String content;

    public TranscriptionDTO() {
    }

    public TranscriptionDTO(Long conversationId, String title, String content) {
        this.conversationId = conversationId;
        this.title = title;
        this.content = content;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
