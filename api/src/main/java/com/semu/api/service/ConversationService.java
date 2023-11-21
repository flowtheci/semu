package com.semu.api.service;

import com.semu.api.model.*;
import com.semu.api.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ConversationService {

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AssistantClient assistantClient;

    @Autowired
    private VoiceClient voiceClient;

    @Autowired
    private VisionClient visionClient;

    public enum JobState {
        WAITING,
        IN_PROGRESS,
        FINISHED,
    }


    private Map<Long, JobState> conversationQueue = new HashMap<>();

    public boolean isJobComplete(Long id) {
        System.out.println("Checking if job " + id + " is complete, state: " + conversationQueue.get(id));
        return conversationQueue.get(id) == JobState.FINISHED;
    }


    public Conversation processQueue(Long id) {
        try {
            System.out.println("Processing job " + id + " with state: " + conversationQueue.get(id));
            Conversation conversation = conversationRepository.findById(id).orElseThrow();
            if (conversationQueue.get(id) == JobState.WAITING) {
                conversationQueue.put(id, JobState.IN_PROGRESS);
                conversation = assistantClient.assist(conversation, Assistants.EstonianAssistant);
                conversationQueue.put(id, JobState.FINISHED);
                System.out.println("Finish processing for " + conversation.getId() + " with new state: " + conversationQueue.get(conversation.getId()));
                return conversationRepository.save(conversation);
            } else return null;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error processing job " + id + " with state: " + conversationQueue.get(id));
            return null;
        }
    }

    public Conversation addMessageAndAnswer(Conversation conversation, Message message, Assistants prompt) {
        conversation.addMessage(message);
        conversation = assistantClient.assist(conversation, prompt);
        return conversationRepository.save(conversation);
    }

    public Conversation addMessageAndAnswer(Conversation conversation, String message) {
        return addMessageAndAnswer(conversation, new Message(message, LocalDateTime.now(), true, conversation), Assistants.MathAssistant);
    }

    public Conversation addMessageAndAnswer(Conversation conversation, String message, Assistants prompt) {
        return addMessageAndAnswer(conversation, new Message(message, LocalDateTime.now(), true, conversation), prompt);
    }

    public Conversation startConversationAndAnswer(User user, String userMessage, Assistants prompt) {
        return startConversationAndAnswer(user, null, userMessage, prompt);
    }

    public Conversation startConversationAndAnswer(User user, String aiMessage, String userMessage) {
        return startConversationAndAnswer(user, aiMessage, userMessage, Assistants.MathAssistant);
    }

    public Conversation startConversationAndAnswer(User user, String aiMessage, String userMessage, Assistants prompt) {
        Conversation conversation = new Conversation();
        conversation.setUser(user);
        if (aiMessage != null) {
            conversation.addMessage(new Message(aiMessage, LocalDateTime.now(), false, conversation));
        }
        conversation.addMessage(new Message(userMessage, LocalDateTime.now(), true, conversation));
        conversation = assistantClient.assist(conversation, prompt);
        System.out.println(conversation);
        return conversationRepository.save(conversation);
    }


    public Conversation startAudioConversation(User user, byte[] audio) {
        String message = voiceClient.transcribeAudio(audio);

        Conversation conversation = new Conversation();
        conversation.setUser(user);
        conversation.addMessage(new Message(message, LocalDateTime.now(), true, conversation));
        conversation = conversationRepository.save(conversation);

        conversationQueue.put(conversation.getId(), JobState.WAITING);
        System.out.println("Added "+ conversation.getId() + " to queue, state: " + conversationQueue.get(conversation.getId()));
        return conversation;
    }

    public Conversation addAudioMessage(Conversation conversation, byte[] audio) {
        String message = voiceClient.transcribeAudio(audio);
        conversation.addMessage(new Message(message, LocalDateTime.now(), true, conversation));
        conversationQueue.put(conversation.getId(), JobState.WAITING);
        System.out.println("Added "+ conversation.getId() + " to queue, state: " + conversationQueue.get(conversation.getId()));
        return conversationRepository.save(conversation);
    }



    public Conversation startImageConversation(User user, String imageBase64) {
        String visionResult = visionClient.analyzeImage(imageBase64);
        return startConversationAndAnswer(user, visionResult, Assistants.MathVisionAssistant);
    }



    public Conversation addImageMessage(Conversation conversation, String imageBase64) {
        String visionResult = visionClient.analyzeImage(imageBase64);
        return addMessageAndAnswer(conversation, visionResult, Assistants.MathVisionAssistant);
    }

    public ReplyDTO getErrorDTO(String userEmail, Long conversationId) {
        User user = userService.getUserByEmail(userEmail);
        ReplyDTO errorReply = new ReplyDTO();
        errorReply.setId(conversationId);
        errorReply.setLastMessageTimestamp(user.getResetTime().toString());
        errorReply.setLastMessage("Kahjuks oled hetkel ületanud SEMU sõnumite piirangu. Kuid ära muretse - saad peatselt uuesti ligipääsu SEMU keskkonnale!");
        return errorReply;
    }


    public ReplyDTO getLastReplyDTO(Conversation conversation) {
        Long id = conversation.getId();
        String lastMessage = conversation.getLastMessage().getContent();
        // byte[] audio = voiceClient.synthesizeVoice(lastMessage);
        return new ReplyDTO(id, lastMessage, String.valueOf(System.currentTimeMillis()));
    }

    public TranscriptionDTO getTranscriptionDTO(Conversation conversation) {
        return new TranscriptionDTO(conversation.getId(), conversation.getTitle(), conversation.getLastUserMessage().getContent());
    }

    public AudioDTO getAudioDTO(Conversation conversation) {
        String lastMessage = conversation.getLastAssistantMessage().getContent();
        byte[] audio = voiceClient.synthesizeVoice(lastMessage);
        return new AudioDTO(lastMessage, audio);
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
        conversations.sort((c1, c2) -> c2.getLastUpdated().compareTo(c1.getLastUpdated()));

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
