package com.semu.api.model;

public class TitleDTO {
    private String title;
    private Long conversationId;

    public TitleDTO() {
    }

    public TitleDTO(String title, Long conversationId) {
        this.title = title;
        this.conversationId = conversationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
}
