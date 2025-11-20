package com.learning.cliente_app.videogenerator.services;

import com.learning.cliente_app.videogenerator.google.GoogleAuthService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class VideoGeneratorService {

    private final GoogleAuthService googleAuthService;
    private final RestTemplate restTemplate = new RestTemplate();

    public VideoGeneratorService(GoogleAuthService googleAuthService) {
        this.googleAuthService = googleAuthService;
    }

    public String generarVideoDesdePrompt(String prompt) {
        try {
            String token = googleAuthService.obtenerAccessToken();

            String url = "https://us-central1-aiplatform.googleapis.com/v1/projects/"
                    + googleAuthService.getProjectId()
                    + "/locations/us-central1/publishers/google/models/gemini-1.5-pro:generateContent";

            Map<String, Object> content = new HashMap<>();
            Map<String, Object> parts = new HashMap<>();
            parts.put("text", "Genera un video basado en este prompt: " + prompt);
            content.put("contents", new Object[]{ Map.of("parts", new Object[]{parts}) });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(token);

            HttpEntity<String> request = new HttpEntity<>(new ObjectMapper().writeValueAsString(content), headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

            return response.getBody();

        } catch (Exception e) {
            return "Error generando video: " + e.getMessage();
        }
    }
}