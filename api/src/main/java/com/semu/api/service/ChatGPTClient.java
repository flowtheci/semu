package com.semu.api.service;

import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatGPTClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;


    public Message getAiResponse(Conversation conversation) {
        return new Message("This is an AI response", LocalDateTime.now(), false, conversation);
    }

    public String generateTitle(Conversation conversation) {
        // TODO request chatgpt api to generate a title based on starting messages.
        return "Title";
    }
}
