package com.semu.api.controller;

import com.semu.api.model.*;
import com.semu.api.service.ConversationService;
import com.semu.api.service.JwtService;
import com.semu.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/startConversation")
    public ResponseEntity<ReplyDTO> startConversation(@RequestHeader(name = "Authorization") String authToken, @RequestBody List<String> messages) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.startConversationAndAnswer(userService.getUserByEmail(email), messages.get(0), Assistants.MathAssistant);
        ReplyDTO answer = conversationService.getLastReplyDTO(conversation);
        if (conversation.getTitle() != null) {
            answer.setTitle(conversation.getTitle());
        }
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/addMessage")
    public ResponseEntity<ReplyDTO> addMessage(@RequestHeader(name = "Authorization") String authToken, @RequestParam Long conversationId, @RequestBody String message) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.addMessageAndAnswer(conversationService.getConversationByIdAndUser(conversationId, email), message);
        return ResponseEntity.ok(conversationService.getLastReplyDTO(conversation));
    }

    @GetMapping("/getConversation")
    public ResponseEntity<ConversationDTO> getConversation(@RequestHeader(name = "Authorization") String authToken, @RequestParam Long conversationId) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.getConversationByIdAndUser(conversationId, email);
        return ResponseEntity.ok(conversationService.getConversationDTO(conversation));
    }

    @GetMapping("/getAllUserConversations")
    public ResponseEntity<List <Long>> getAllConversations(@RequestHeader(name = "Authorization") String authToken) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(conversationService.getConversationIds(email));
    }

    /**
     * Conversation title util
     */

    @GetMapping("/getConversationTitles")
    public ResponseEntity<HashMap<Long, String>> getConversationTitles(@RequestHeader(name = "Authorization") String authToken, @RequestBody List<Long> conversationIds) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(conversationService.getConversationTitlesForUser(conversationIds, email));
    }

    @GetMapping("/getLastConversationTitles")
    public ResponseEntity<HashMap<Long, String>> getConversationTitles(@RequestHeader(name = "Authorization") String authToken, @RequestParam Long amount) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(conversationService.getLastConversationTitlesForUser(email, amount));
    }

    /**
     * Audio conversation endpoints
     */

    @PostMapping("/startAudioConversation")
    public ResponseEntity<TranscriptionDTO> startAudioConversation(@RequestHeader(name = "Authorization") String authToken, @RequestParam("audioMessage") MultipartFile audioFile) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Conversation conversation = conversationService.startAudioConversation(userService.getUserByEmail(email), audioFile.getBytes());
            TranscriptionDTO answer = conversationService.getTranscriptionDTO(conversation);
            return ResponseEntity.ok(answer);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/addAudioMessage")
    public ResponseEntity<TranscriptionDTO> addAudioMessage(@RequestHeader(name = "Authorization") String authToken, @RequestParam Long conversationId, @RequestParam("audioMessage") MultipartFile audioFile) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        try {
            Conversation conversation = conversationService.addAudioMessage(conversationService.getConversationByIdAndUser(conversationId, email), audioFile.getBytes() );
            return ResponseEntity.ok(conversationService.getTranscriptionDTO(conversation));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/getAudioResponse")
    public ResponseEntity<AudioDTO> getAudioResponse(@RequestHeader(name = "Authorization") String authToken, @RequestParam Long conversationId) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.processQueue(conversationId);
        return ResponseEntity.ok(conversationService.getAudioDTO(conversation));
    }

    /**
     * Image conversation endpoints
     */

    @PostMapping("/startImageConversation")
    public ResponseEntity<ReplyDTO> startImageConversation(@RequestHeader(name = "Authorization") String authToken, @RequestBody String imageBase64) {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.startImageConversation(userService.getUserByEmail(email), imageBase64);
        ReplyDTO answer = conversationService.getLastReplyDTO(conversation);
        if (conversation.getTitle() != null) {
            answer.setTitle(conversation.getTitle());
        }
        return ResponseEntity.ok(answer);
    }

    @PostMapping("/addImageMessage")
    public ResponseEntity<ReplyDTO> addImageMessage(@RequestHeader(name = "Authorization") String authToken,
                                                    @RequestParam Long conversationId,
                                                    @RequestBody String imageBase64)
    {
        String email = jwtService.validateTokenAndGetSubject(authToken.substring(7));
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        Conversation conversation = conversationService.addImageMessage(conversationService.getConversationByIdAndUser(conversationId, email), imageBase64);
        ReplyDTO answer = conversationService.getLastReplyDTO(conversation);
        if (conversation.getTitle() != null) {
            answer.setTitle(conversation.getTitle());
        }
        return ResponseEntity.ok(answer);
    }


}
