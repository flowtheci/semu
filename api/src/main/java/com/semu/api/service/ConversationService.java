package com.semu.api.service;

import com.semu.api.model.*;
import com.semu.api.repository.ConversationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatGPTClient chatGPTClient;

    public Conversation addMessageAndAnswer(Conversation conversation, Message message) {
        conversation.addMessage(message);
        conversation.addMessage(chatGPTClient.getAiResponse(conversation));
        return conversationRepository.save(conversation);
    }

    public Conversation addMessageAndAnswer(Conversation conversation, String message) {
        conversation.addMessage(new Message(message, LocalDateTime.now(), true, conversation));
        conversation.addMessage(chatGPTClient.getAiResponse(conversation));
        return conversationRepository.save(conversation);
    }

    public Conversation startConversationAndAnswer(User user, String aiMessage, String userMessage) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.addMessage(new Message(aiMessage, LocalDateTime.now(), false, conversation));
        conversation.addMessage(new Message(userMessage, LocalDateTime.now(), true, conversation));
        conversation.addMessage(chatGPTClient.getAiResponse(conversation));
        conversation.setTitle(chatGPTClient.generateTitle(conversation));
        return conversationRepository.save(conversation);
    }

    public ReplyDTO getLastReplyDTO(Conversation conversation) {
        Long id = conversation.getId();
        String lastMessage = conversation.getMessages().get(conversation.getMessages().size() - 1).getContent();

        return new ReplyDTO(id, lastMessage, String.valueOf(System.currentTimeMillis()));
    }

    public ConversationDTO getConversationDTO(Conversation conversation) {
        List<MessageDTO> messageDTOs = conversation.getMessages().stream().map(message -> new MessageDTO(message.getId(), message.getContent(), message.getTimestamp().toString(), message.isUser())).toList();
        return new ConversationDTO(conversation.getId(), conversation.getTitle(), messageDTOs, LocalDateTime.now().toString());
    }

    public HashMap<Long, String> getConversationTitlesForUser(List<Long> titleIds, String email) {
        List<Conversation> conversations = getConversationsByUser(email);
        HashMap<Long, String> conversationTitles = new HashMap<>();
        for (Conversation conversation : conversations) {
            if (titleIds.contains(conversation.getId())) {
                conversationTitles.put(conversation.getId(), conversation.getTitle());
            }
        }
        return conversationTitles;
    }

    public HashMap<Long, String> getLastConversationTitlesForUser(String email, Long amount) {
        List<Conversation> conversations = getConversationsByUser(email);
        HashMap<Long, String> conversationTitles = new HashMap<>();
        for (Conversation conversation : conversations) {
            if (conversationTitles.size() >= amount) {
                break;
            }
            conversationTitles.put(conversation.getId(), conversation.getTitle());
        }
        return conversationTitles;
    }





    public Conversation getConversationByIdAndUser(Long id, String email) {
        User user = userService.getUserByEmail(email);
        return conversationRepository.findByIdAndUser(id, user);
    }

    public List<Conversation> getConversationsByUser(String email) {
        User user = userService.getUserByEmail(email);
        return conversationRepository.findByUser(user);
    }

    public List<Long> getConversationIds(String email) {
        List<Conversation> conversations = getConversationsByUser(email);
        if (conversations == null) return new ArrayList<>();
        return conversations.stream().map(Conversation::getId).toList();
    }


    public List<Conversation> getAllConversations() {
        return conversationRepository.findAll();
    }

    public Conversation updateConversation(Conversation conversation) {
        return conversationRepository.save(conversation);
    }

    public void deleteConversation(Long id) {
        conversationRepository.deleteById(id);
    }
}
