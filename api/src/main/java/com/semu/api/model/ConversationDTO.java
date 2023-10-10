package com.semu.api.model;

import java.sql.Blob;
import java.util.List;

public class ConversationDTO {

        private Long conversationId;
        private String title;
        private List<MessageDTO> messages;
        private String lastMessageTimestamp;


        public ConversationDTO() {
        }

        public ConversationDTO(Long conversationId, String title, List<MessageDTO> messages, String lastMessageTimestamp) {
            this.conversationId = conversationId;
            this.title = title;
            this.messages = messages;
            this.lastMessageTimestamp = lastMessageTimestamp;
        }

        public Long getConversationId() {
            return conversationId;
        }

        public String getTitle() {
            return title;
        }

        public List<MessageDTO> getMessages() {
            return messages;
        }

        public String getLastMessageTimestamp() {
            return lastMessageTimestamp;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setConversationId(Long conversationId) {
            this.conversationId = conversationId;
        }

        public void setMessages(List<MessageDTO> messages) {
            this.messages = messages;
        }

        public void setLastMessageTimestamp(String lastMessageTimestamp) {
            this.lastMessageTimestamp = lastMessageTimestamp;
        }
}
