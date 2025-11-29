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

    @Value("${gemini.api.key}")
    private String GEMINI_API_KEY;

    @Value("${gemini.api.host:https://generativelanguage.googleapis.com}")
    private String GEMINI_API_HOST;

    private final ExtractService extractService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final com.learning.cliente_app.resumen.repository.ResumenRepository resumenRepository;
    private final com.learning.cliente_app.user.repository.UserRepository userRepository;

    public ResumenService(ExtractService extractService, RestTemplate restTemplate,
            com.learning.cliente_app.resumen.repository.ResumenRepository resumenRepository,
            com.learning.cliente_app.user.repository.UserRepository userRepository) {
        this.extractService = extractService;
        this.restTemplate = restTemplate;
        this.resumenRepository = resumenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Genera un resumen a partir de un texto dado.
     */
    public String resumirTexto(String texto, Long userId) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto no puede estar vacío");
        }
        String resumen = callGeminiSummaryAPI(texto);

        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> {
                com.learning.cliente_app.resumen.model.ResumenEntity entity = new com.learning.cliente_app.resumen.model.ResumenEntity(
                        user, "Resumen Generado", resumen);
                resumenRepository.save(entity);
            });
        }

        return resumen;
    }

    // Overload for backward compatibility if needed, or just update callers.
    public String resumirTexto(String texto) {
        return resumirTexto(texto, null);
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
            return callGeminiSummaryAPI(extractedText);
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo para resumen: " + e.getMessage(), e);
        }
    }

    private String callGeminiSummaryAPI(String text) {
        try {
            // Gemini API endpoint - usando gemini-pro
            String apiUrl = GEMINI_API_HOST + "/v1beta/models/gemini-pro:generateContent?key=" + GEMINI_API_KEY;

            String prompt = "Genera un resumen conciso y bien estructurado del siguiente texto. " +
                    "Resalta los puntos clave y las conclusiones principales:\n\n" + text;

            // Construir el request body según el formato de Gemini
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Object[] { part });

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", new Object[] { content });

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Error al generar resumen con Gemini — HTTP " + response.getStatusCode());
            }

            // Parsear respuesta de Gemini
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            JsonNode candidates = jsonResponse.get("candidates");
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode content_node = candidates.get(0).get("content");
                if (content_node != null) {
                    JsonNode parts = content_node.get("parts");
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
            throw new RuntimeException("Error al conectar con Gemini para resumen: " + e.getMessage(), e);
        }
    }
}
