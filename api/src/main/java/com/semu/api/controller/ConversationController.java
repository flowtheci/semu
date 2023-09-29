package com.semu.api.controller;

import com.semu.api.model.Conversation;
import com.semu.api.model.ConversationDTO;
import com.semu.api.model.ReplyDTO;
import com.semu.api.service.ConversationService;
import com.semu.api.service.JwtService;
import com.semu.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

        Conversation conversation = conversationService.startConversationAndAnswer(userService.getUserByEmail(email), messages.get(0), messages.get(1));
        return ResponseEntity.ok(conversationService.getLastReplyDTO(conversation));
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
}
