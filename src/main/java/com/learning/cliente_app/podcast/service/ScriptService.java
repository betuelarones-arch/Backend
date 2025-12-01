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

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.api.host:https://api.openai.com/v1}")
    private String OPENAI_API_HOST;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Genera un guion profesional basado en un texto base
     */
    public String generateScript(String textoBase) {
        return callOpenAIScriptAPI(
                "Genera un guion profesional para video educativo basado en el siguiente texto: " + textoBase);
    }

    /**
     * Genera un guion a partir de un tema o prompt
     */
    public String generateScriptFromPrompt(String prompt) {
        return callOpenAIScriptAPI(
                "Genera un guion profesional para video educativo basado en el siguiente tema: " + prompt);
    }

    /**
     * Llama a la API de Gemini (generateContent)
     */
    /**
     * Llama a la API de OpenAI (chat completions)
     */
    private String callOpenAIScriptAPI(String content) {
        try {
            // Construir URL con API key
            String apiUrl = OPENAI_API_HOST + "/chat/completions";

            // Construir request body para OpenAI
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", content);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("messages", new Object[] { message });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error al generar el guion con OpenAI — HTTP " + response.getStatusCode());
            }

            // Parsear respuesta de OpenAI
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode messageNode = choices.get(0).get("message");
                if (messageNode != null) {
                    JsonNode contentNode = messageNode.get("content");
                    if (contentNode != null) {
                        return contentNode.asText().trim();
                    }
                }
            }

            throw new RuntimeException("Respuesta inesperada de OpenAI: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el guion con OpenAI: " + e.getMessage(), e);
        }
    }
}
