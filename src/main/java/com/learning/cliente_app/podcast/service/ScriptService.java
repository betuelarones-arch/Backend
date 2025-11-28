package com.learning.cliente_app.podcast.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScriptService {

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.api.host:https://generativelanguage.googleapis.com}")
    private String GEMINI_API_HOST;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Genera un guion profesional basado en un texto base
     */
    public String generateScript(String textoBase) {
        return callGeminiScriptAPI(
                "Genera un guion profesional para video educativo basado en el siguiente texto: " + textoBase);
    }

    /**
     * Genera un guion a partir de un tema o prompt
     */
    public String generateScriptFromPrompt(String prompt) {
        return callGeminiScriptAPI(
                "Genera un guion profesional para video educativo basado en el siguiente tema: " + prompt);
    }

    /**
     * Llama a la API de Gemini (generateContent)
     */
    private String callGeminiScriptAPI(String content) {
        try {
            // Construir URL con API key
            String apiUrl = GEMINI_API_HOST + "/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;

            // Construir request body para Gemini
            Map<String, Object> part = new HashMap<>();
            part.put("text", content);

            Map<String, Object> contentObj = new HashMap<>();
            contentObj.put("parts", new Object[] { part });

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[] { contentObj });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error al generar el guion con Gemini — HTTP " + response.getStatusCode());
            }

            // Parsear respuesta de Gemini
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode contentNode = candidates.get(0).get("content");
                if (contentNode != null) {
                    JsonNode parts = contentNode.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode textNode = parts.get(0).get("text");
                        if (textNode != null) {
                            return textNode.asText().trim();
                        }
                    }
                }
            }

            throw new RuntimeException("Respuesta inesperada de Gemini: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el guion con Gemini: " + e.getMessage(), e);
        }
    }
}
