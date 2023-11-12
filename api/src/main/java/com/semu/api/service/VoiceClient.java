package com.semu.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class VoiceClient {

    @Value("${chatgpt.api.key}")
    private String apiKey;

    @Value("${chatgpt.api.old-url}")
    private String apiUrl;

    @Value("${speech.api.url}")
    private String speechApiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public VoiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    public String transcribeAudio(byte[] audioFile) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("model", "whisper-1");
        body.add("language", "et");
        body.add("prompt", "Hei! Mina olen SEMU, sinu virtuaalne abiline. Kuidas saan sind aidata?");

        // Wrap the byte array in a ByteArrayResource for the 'file' part
        ByteArrayResource fileResource = new ByteArrayResource(audioFile) {
            @Override
            public String getFilename() {
                return "audio.wav"; // You need to provide a filename for the multipart file upload
            }
        };

        // Add the ByteArrayResource to the body
        body.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        String url = apiUrl + "/audio/transcriptions";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<String, String> responseBody = response.getBody();
            assert responseBody != null;
            System.out.println(responseBody.get("text"));
            return responseBody.get("text");
        } catch (Exception e) {
            throw new RuntimeException("Error calling OpenAI API:", e);
        }
    }


    public byte[] synthesizeVoice(String text) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("speaker", "tambet");
        body.put("speed", 1.25);
        body.put("text", text);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<byte[]> response = restTemplate.postForEntity(speechApiUrl, request, byte[].class);
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error calling Neurokõne API:", e);
        }
    }
}
