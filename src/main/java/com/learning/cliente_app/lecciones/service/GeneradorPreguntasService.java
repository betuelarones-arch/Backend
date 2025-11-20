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


@Service
public class GeneradorPreguntasService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.api.host:https://api.openai.com}")
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
     * Genera preguntas desde OpenAI y las devuelve como lista de DTOs
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

        // Construir request a OpenAI
        JsonNode root = objectMapper.createObjectNode()
                .put("model", "gpt-4o-mini")
                .set("messages", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt)
                        ));

        String body = objectMapper.writeValueAsString(root);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_HOST + "/v1/chat/completions"))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() != 200) {
            throw new RuntimeException("Error al generar preguntas: HTTP " + resp.statusCode() + " - " + resp.body());
        }

        // Obtener contenido de la respuesta
        JsonNode json = objectMapper.readTree(resp.body());
        String resultado = json
                .path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();

        // Limpiar markdown si existe
        resultado = limpiarBloqueCodigo(resultado);

        // Convertir a lista de DTOs
        try {
            return objectMapper.readValue(resultado, new TypeReference<List<PreguntaDTO>>() {});
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
        if (texto == null) return "";
        return texto
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}