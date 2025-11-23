package com.learning.cliente_app.resumen.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.cliente_app.podcast.service.ExtractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ResumenService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private final ExtractService extractService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResumenService(ExtractService extractService, RestTemplate restTemplate) {
        this.extractService = extractService;
        this.restTemplate = restTemplate;
    }

    /**
     * Genera un resumen a partir de un texto dado.
     */
    public String resumirTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        return callOpenAISummaryAPI(texto);
    }

    /**
     * Genera un resumen a partir de un archivo (PDF, DOCX, etc.).
     */
    public String resumirArchivo(MultipartFile file) {
        try {
            String extractedText = extractService.extractText(file);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("No se pudo extraer texto del archivo o está vacío");
            }
            return callOpenAISummaryAPI(extractedText);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo para resumen: " + e.getMessage(), e);
        }
    }

    private String callOpenAISummaryAPI(String text) {
        try {
            String apiUrl = "https://api.openai.com/v1/chat/completions";

            String prompt = "Genera un resumen conciso y bien estructurado del siguiente texto. " +
                    "Resalta los puntos clave y las conclusiones principales:\n\n" + text;

            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "gpt-4o-mini"); // Usamos mini para rapidez y coste
            requestBody.put("messages", new Object[] { message });
            requestBody.put("temperature", 0.5);
            requestBody.put("max_tokens", 1000);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(OPENAI_API_KEY);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error al generar resumen con OpenAI — HTTP " + response.getStatusCode());
            }

            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode choices = jsonResponse.get("choices");
            if (choices != null && choices.isArray() && choices.size() > 0) {
                JsonNode msg = choices.get(0).get("message");
                if (msg != null && msg.has("content")) {
                    return msg.get("content").asText().trim();
                }
            }

            throw new RuntimeException("Respuesta inesperada de OpenAI: " + response.getBody());

        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con OpenAI para resumen: " + e.getMessage(), e);
        }
    }
}
