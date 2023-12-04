package com.semu.api.service;


import com.semu.api.model.Conversation;
import com.semu.api.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class VisionClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.old-url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public VisionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String analyzeImage(String imageBase64) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String prompt = getMathVisionPrompt();
        List<Map<String, Object>> content = new ArrayList<>();
        content.add(Map.of("type", "text", "text", prompt));
        content.add(Map.of("type", "image_url", "image_url", Map.of("url", "data:image/jpeg;base64," + imageBase64)));

        List<Map<String, Object>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content",  content));


        Map<String, Object> body = new HashMap<>();
        body.put("model", "gpt-4-vision-preview");
        body.put("messages", messages);
        body.put("max_tokens", 2000);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        String url = apiUrl + "/chat/completions";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            Map<String, Object> choice = choices.get(0);

            String result = ((LinkedHashMap<String, String>) choice.get("message")).get("content");
            System.out.println(responseBody);
            System.out.println(result);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API:", e);
        }
    }

    public static String getMathVisionPrompt() {
        return "Sa oled matemaatika visuaalne assistant teisele AI teenusele mis on matemaatiline assistant. Sinu ülesandeks" +
                " on analüüsida pilte mis sulle saadetakse, ning tagastada vastus mis sisaldab ainult antud pildi matemaatilist sisu." +
                " Pilt võib olla näiteks matemaatika ülesanne, graafik, tabel, joonis, tekstülesanne jne. Sinu ülesanne EI OLE lahendada" +
                " matemaatika ülesannet, vaid analüüsida pilti ning tagastada vastus mis sisaldab vaid olulist pildi matemaatilist sisu nii lühidalt ja täpselt kui võimalik." +
                " Matemaatiliste elementide kuvamiseks kasuta vormistuseks LaTeX (Mathjax ehk ainult matemaatiliste elementide) kuju, mitte Markdowni. Näiteks: $x^2$ mitte x^2. " +
                "Jällegi, vastuses peaks olema võimalikult vähe sõnu, ning peaks kirjeldama vaid pildil olevaid elemente mis on vaja ülesande lahendamiseks. " +
                "Matemaatikaga mitte seotud piltide analüüsimisel tagasta järgnev:" +
                "\"Pildi analüüsimine ebaõnnestus, kuna (PÕHJUS).\" - kus (PÕHJUS) on ebaõnnestumise põhjus.";

    }
}
