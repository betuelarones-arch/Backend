package com.learning.cliente_app.lecciones.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.cliente_app.lecciones.dto.PreguntaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class GeneradorPreguntasService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.api.host:https://api.openai.com/v1}")
    private String OPENAI_API_HOST;

    // Configurables desde application.properties
    @Value("${app.preguntas.minimo:5}")
    private int MIN_PREGUNTAS;

    @Value("${app.preguntas.maximo:20}")
    private int MAX_PREGUNTAS;

    @Value("${app.preguntas.por-defecto:10}")
    private int DEFAULT_PREGUNTAS;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Genera preguntas desde Gemini y las devuelve como lista de DTOs
     */
    public List<PreguntaDTO> generarPreguntas(String texto, int numPreguntas) throws Exception {

        if (texto == null || texto.trim().isEmpty()) {
            throw new RuntimeException("El texto para generar preguntas está vacío");
        }

        // Validar y ajustar número de preguntas
        numPreguntas = validarNumeroPreguntas(numPreguntas);

        String prompt = String.format("""
                Eres un asistente de educación.
                Crea EXACTAMENTE %d preguntas tipo quiz con 4 opciones cada una.
                Devuelve SOLO un JSON en forma de lista, sin texto adicional:

                [
                    {
                        "pregunta": "texto de la pregunta",
                        "opciones": ["opción A", "opción B", "opción C", "opción D"],
                        "respuestaCorrecta": "opción correcta"
                    }
                ]

                IMPORTANTE:
                - No incluyas ningún texto antes o después del JSON
                - No uses bloques de código markdown
                - Devuelve solo el array JSON
                - Genera EXACTAMENTE %d preguntas, ni más ni menos

                Contenido del cual generar las preguntas:
                %s
                """, numPreguntas, numPreguntas, texto);

        // Construir request body para OpenAI
        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini");
        requestBody.put("messages", new Object[] { message });

        String body = objectMapper.writeValueAsString(requestBody);

        // Construir URL con API key
        String apiUrl = OPENAI_API_HOST + "/chat/completions";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("Error al generar preguntas: HTTP " + resp.statusCode() + " - " + resp.body());
        }

        // Parsear respuesta de OpenAI
        JsonNode json = objectMapper.readTree(resp.body());
        JsonNode choices = json.path("choices");
        if (choices.isEmpty() || !choices.isArray()) {
            throw new RuntimeException("Respuesta inesperada de OpenAI: " + resp.body());
        }

        String resultado = choices
                .get(0)
                .path("message")
                .path("content")
                .asText();

        // Limpiar markdown si existe
        resultado = limpiarBloqueCodigo(resultado);

        // Convertir a lista de DTOs
        try {
            return objectMapper.readValue(resultado, new TypeReference<List<PreguntaDTO>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Error al parsear preguntas generadas. JSON recibido: " + resultado, e);
        }
    }

    /**
     * Valida que el número de preguntas esté dentro de los límites permitidos
     */
    private int validarNumeroPreguntas(int numPreguntas) {
        if (numPreguntas < MIN_PREGUNTAS) {
            return MIN_PREGUNTAS;
        }
        if (numPreguntas > MAX_PREGUNTAS) {
            return MAX_PREGUNTAS;
        }
        return numPreguntas;
    }

    /**
     * Obtiene el número por defecto de preguntas
     */
    public int getDefaultPreguntas() {
        return DEFAULT_PREGUNTAS;
    }

    /**
     * Obtiene el límite mínimo de preguntas
     */
    public int getMinPreguntas() {
        return MIN_PREGUNTAS;
    }

    /**
     * Obtiene el límite máximo de preguntas
     */
    public int getMaxPreguntas() {
        return MAX_PREGUNTAS;
    }

    private String limpiarBloqueCodigo(String texto) {
        if (texto == null)
            return "";
        return texto
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}