package com.semu.api.service;

import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatGPTClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Value("${chatgpt.api.old-url}")
    private String oldApiUrl;

    @Value("${chatgpt.assistant.math}")
    private String mathAssistantId;

    @Value("${chatgpt.assistant.title}")
    private String titleAssistantId;

    @Value("${chatgpt.assistant.estonian}")
    private String estonianAssistantId;

    private final RestTemplate restTemplate;

    @Autowired
    public ChatGPTClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Conversation assist(Conversation conversation) throws RuntimeException {
        return assist(conversation, true);
    }

    public Conversation assist(Conversation conversation, boolean useMathModule) throws RuntimeException {
        if (conversation.getThreadId() == null) {
            conversation.setThreadId(createEmptyThread());
        }

        addLastMessageToThread(conversation);
        run(conversation, useMathModule);

        if (conversation.getTitle() == null) {
            conversation.setTitle(generateTitle(conversation));
        }
        return conversation;
    }


    private void run(Conversation conversation, boolean useMathModule) throws RuntimeException {
        HttpHeaders headers = headers();
        String assistantId = useMathModule ? mathAssistantId : estonianAssistantId;
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

        if (Instant.now().isAfter(timestamp.plusSeconds(30))) {
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
        HttpHeaders headers = headers(false);

        String prompt = getTitlePrompt();
        List<Map<String, String>> finalMessages = new ArrayList<>();
        finalMessages.add(Map.of("role", "system", "content", prompt));

        for (Message message : conversation.getMessages()) {
            Map<String, String> msg = new HashMap<>();
            msg.put("role", message.isUser() ? "user" : "assistant");
            msg.put("content", message.getContent());
            finalMessages.add(msg);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-3.5-turbo");
        body.put("messages", finalMessages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(oldApiUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> choice = choices.get(0);

           String result = ((LinkedHashMap<String, String>) choice.get("message")).get("content");
           System.out.println(result);

           return result;
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API:", e);
        }
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
    private String getTitlePrompt() {
        return "Sina oled TitleGPT: Sinu ainus eesmärk on luua pealkiri vestlusele mis hõlmab mida kasutaja AI õpirobotilt küsis võimalikult lihtsalt. Pealkiri peaks alati jääma alla 5 sõna. Vestluse kuju põhjal tagasta vaid võimalik pealkiri ning ära lisa vastusesse mingit muud teksti. Ära sisalda vastuses jutumärke ega ühtegi muud sümbolit mis pealkirja ei sobiks. Vasta AINULT pealkirjaga.";
    }

    }
