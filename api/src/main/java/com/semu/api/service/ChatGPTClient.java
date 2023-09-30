package com.semu.api.service;

import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import com.semu.api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ChatGPTClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.url}")
    private String apiUrl;

    @Value("${chatgpt.model}")
    private String model;

    private final RestTemplate restTemplate;

    @Autowired
    public ChatGPTClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public Message getAiResponse(Conversation conversation) throws RuntimeException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = getMathPrompt(conversation.getUser());
        List<Map<String, String>> finalMessages = new ArrayList<>();
        finalMessages.add(Map.of("role", "system", "content", prompt));

        for (Message message : conversation.getMessages()) {
            Map<String, String> msg = new HashMap<>();
            msg.put("role", message.isUser() ? "user" : "assistant");
            msg.put("content", message.getContent());
            finalMessages.add(msg);
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", finalMessages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> choice = choices.get(0);
            System.out.println(choice);
            String result = ((LinkedHashMap<String, String>) choice.get("message")).get("content");

            return new Message(result, LocalDateTime.now(), false, conversation);
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API:", e);
        }
    }

    public String generateTitle(Conversation conversation) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

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
        body.put("model", model);
        body.put("messages", finalMessages);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);
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

    private String getMathPrompt(User user) {
        return "Sa oled matemaatika õpetaja nimega Semu, kes aitab hetkel õpilast nimega " + user.getFirstName() + " ainult matemaatiliste ülesannetega. Sinu eesmärgiks on õpetada kasutajat lahendandama matemaatilisi ülesandeid ning selgeks tegema kuidas ülesandeid lahendada. Sa tohid vastata ainult matemaatikaga seotud küsimustele ning mitte millelegi muule. Semu on loodud Eesti keeles, nii et sa peaksid rääkima vaid Eesti keeles. Palved sulle on eesti keelsed. Jaota vastus mitmeks osaks, et neid saaks avaldada õpilasele veebilehel samm sammult vajutades nuppu avalda. Sinu ülesanne on õpetada 4. klassi õpilast, kasuta sõnavara, mis aitab 4.klassi õpilasel paremini ülesannetest aru saada Too näiteid toetamaks teoreetilisi ülesandeid. Sinu ülesanne on selle ülesandega seostuvad teooriat selgitada, kuid ära näita kasutajale vastust, võid kasutada näiteks muude numbritega ülesannet.";

    }

    private String getTitlePrompt() {
        return "Sina oled TitleGPT: Sinu ainus eesmärk on luua pealkiri vestlusele mis hõlmab mida kasutaja AI õpirobotilt küsis võimalikult lihtsalt. Pealkiri peaks alati jääma alla 5 sõna. Vestluse kuju põhjal tagasta vaid võimalik pealkiri ning ära lisa vastusesse mingit muud teksti.";

    }
}
