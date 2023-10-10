package com.semu.api.service;

import com.google.cloud.speech.v1.*;
import com.google.protobuf.ByteString;
import com.semu.api.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class VoiceClient {

    private final RestTemplate restTemplate;

    @Value("${speech.api.url}")
    private String speechApiUrl;

    @Autowired
    public VoiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String transcribeVoice(byte[] audioBytes) {
        try (SpeechClient speechClient = SpeechClient.create()) {
            StringBuilder transcript = new StringBuilder();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.FLAC)
                    .setLanguageCode("et-EE")
                    .build();

            RecognizeRequest request = RecognizeRequest.newBuilder()
                    .setConfig(config)
                    .setAudio(audio)
                    .build();

            RecognizeResponse response = speechClient.recognize(request);

            for (SpeechRecognitionResult result : response.getResultsList()) {
                SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
                transcript.append(alternative.getTranscript());
            }
            return transcript.toString();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error transcribing voice data:", e);
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
            saveToFile(response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Error calling Neurokõne API:", e);
        }
    }

    public void saveToFile(byte[] audioData) throws IOException {
        Path path = Paths.get("test.wav");  // Change the extension if it's not MP3
        Files.write(path, audioData);
    }

}
