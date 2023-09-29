package com.semu.api.service;

import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatGPTClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;


    public String getAiResponse(Conversation conversation) {
        return "";
    }
}
