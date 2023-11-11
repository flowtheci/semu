package com.semu.api.service;

import com.semu.api.config.AssistantProperties;
import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import com.semu.api.model.Assistants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AssistantClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Autowired
    private AssistantProperties assistantProperties;

    private final RestTemplate restTemplate;

    @Autowired
    public AssistantClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Conversation assist(Conversation conversation) throws RuntimeException {
        return assist(conversation, Assistants.MathAssistant);
    }

    public Conversation assist(Conversation conversation, Assistants prompt) throws RuntimeException {
        if (conversation.getThreadId() == null) {
            conversation.setThreadId(createEmptyThread());
        }

        addLastMessageToThread(conversation);
        run(conversation, prompt);

        if (conversation.getTitle() == null) {
            conversation.setTitle(generateTitle(conversation));
        }
        return conversation;
    }


    private void run(Conversation conversation, Assistants prompt) throws RuntimeException {
        HttpHeaders headers = headers();
        String assistantId = assistantProperties.get(prompt);
        String runUrl = String.format("%s/%s/runs", apiUrl, conversation.getThreadId());
        Map<String, Object> body = new HashMap<>();
        body.put("assistant_id", assistantId);

        Instant timestamp = Instant.now();


        String runId = doPostRequest(runUrl, new HttpEntity<>(body, headers), "id");
        while (!checkIfRunFinished(runId, conversation.getThreadId()) && Instant.now().isBefore(timestamp.plusSeconds(30))) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (Instant.now().isAfter(timestamp.plusSeconds(60))) {
            throw new RuntimeException("Run took too long, cancelled.");
        }

        updateConversationWithResponse(conversation);
    }

    private boolean checkIfRunFinished(String runId, String threadId) throws RuntimeException {
        String runUrl = String.format("%s/%s/runs/%s", apiUrl, threadId, runId);
        String runStatus = doGetRequest(runUrl, "status", String.class);
        System.out.println("Run " + runId + " on thread " + threadId + " has status: " + runStatus);
        if (runStatus != null && runStatus.equals("failed")) {
            throw new RuntimeException("Run status failed.");
        }
        return runStatus != null && runStatus.equals("completed");
    }

    private String createEmptyThread() throws RuntimeException {
        HttpHeaders headers = headers();
        Map<String, Object> body = new HashMap<>();

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);


        return doPostRequest(apiUrl, request, "id");
    }

    private void addLastMessageToThread(Conversation conversation) throws RuntimeException {
        HttpHeaders headers = headers();
        Map<String, Object> body = new HashMap<>();
        Message lastMessage = conversation.getMessages().get(conversation.getMessages().size() - 1);
        body.put("content", lastMessage.getContent());
        body.put("role", "user");

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String messageUrl = String.format("%s/%s/messages", apiUrl, conversation.getThreadId());

        String threadMessageId = doPostRequest(messageUrl, request, "id");
        lastMessage.setThreadMessageId(threadMessageId);
    }

    private void updateConversationWithResponse(Conversation conversation) throws RuntimeException {
        String lastUserThreadMessageId = conversation.getMessages().get(conversation.getMessages().size() - 1).getThreadMessageId();
        String listMessageUrl = String.format("%s/%s/messages?after=%s&order=asc", apiUrl, conversation.getThreadId(), lastUserThreadMessageId);

        ArrayList<HashMap<String, Object>> messages = doGetRequest(listMessageUrl, "data", ArrayList.class);
        for (HashMap<String, Object> message : messages) {
            StringBuilder content = new StringBuilder();
            for (Object obj : (ArrayList<Object>) message.get("content")) {
                String value = ((LinkedHashMap) ((LinkedHashMap<String, Object>) obj).get("text")).get("value").toString();
                content.append(value).append('\n');
            }
            String role = (String) message.get("role");
            String threadMessageId = (String) message.get("id");

            Message newMessage = new Message(content.toString(), LocalDateTime.now(), role.equals("user"), conversation);
            newMessage.setThreadMessageId(threadMessageId);
            conversation.addMessage(newMessage);
        }
    }



    private String generateTitle(Conversation conversation) {
        String threadId = createEmptyThread();
        String oldId = conversation.getThreadId();
        conversation.setThreadId(threadId);
        addLastMessageToThread(conversation);
        run(conversation, Assistants.TitleAssistant);
        String result = conversation.getLastMessage().getContent();
        conversation.removeMessage(conversation.getLastMessage());
        conversation.setThreadId(oldId);
        return result;
    }


    private HttpHeaders headers(boolean beta) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (beta) {
            headers.set("OpenAI-Beta", "assistants=v1");
        }
        return headers;
    }

    private HttpHeaders headers() {
        return headers(true);
    }

    private <X> X doGetRequest(String url, String returnGetter, Class<X> returnType) throws RuntimeException {
        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers()), Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (X) response.getBody().get(returnGetter);
            } else {
                throw new RuntimeException("Error creating thread: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating thread:", e);
        }
    }

    private String doPostRequest(String url, HttpEntity<Map<String, Object>> request, String returnGetter) throws RuntimeException {
        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return (String) response.getBody().get(returnGetter);
            } else {
                throw new RuntimeException("Error creating thread: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error creating thread:", e);
        }
    }
}
