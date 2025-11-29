package com.learning.cliente_app.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learning.cliente_app.chatbot.dto.ChatResponse;
import com.learning.cliente_app.chatbot.model.Conversacion;
import com.learning.cliente_app.chatbot.model.Mensaje;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatbotService {

    private final Map<String, Conversacion> conversaciones = new ConcurrentHashMap<>();
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.host}")
    private String geminiApiHost;

    private static final String SYSTEM_PROMPT = "Eres un asistente educativo especializado. SOLO puedes responder preguntas relacionadas con:\n"
            +
            "- Educación, aprendizaje y enseñanza\n" +
            "- Materias académicas (matemáticas, ciencias, historia, lengua, etc.)\n" +
            "- Métodos de estudio y técnicas pedagógicas\n" +
            "- Consejos para estudiantes y profesores\n" +
            "- Recursos educativos\n\n" +
            "Si te preguntan sobre temas NO educativos (deportes, entretenimiento, política, etc.), " +
            "responde amablemente que solo puedes ayudar con temas educativos y pregunta si tienen " +
            "alguna consulta relacionada con educación.";

    public ChatbotService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String crearNuevaConversacion() {
        String id = UUID.randomUUID().toString();
        conversaciones.put(id, new Conversacion(id));
        return id;
    }

    public ChatResponse procesarMensaje(String conversacionId, String mensajeUsuario) {
        Conversacion conversacion = conversaciones.computeIfAbsent(
                conversacionId,
                k -> new Conversacion(k));

        conversacion.agregarMensaje(new Mensaje("user", mensajeUsuario));

        String respuesta = obtenerRespuestaGemini(conversacion).block();

        conversacion.agregarMensaje(new Mensaje("model", respuesta)); // Gemini usa 'model' en lugar de 'assistant'

        return new ChatResponse(conversacionId, respuesta, true, LocalDateTime.now());
    }

    private Mono<String> obtenerRespuestaGemini(Conversacion conversacion) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();

            // System Instruction (Prompt del sistema)
            ObjectNode systemInstruction = requestBody.putObject("systemInstruction");
            ArrayNode systemParts = systemInstruction.putArray("parts");
            systemParts.addObject().put("text", SYSTEM_PROMPT);

            // Contents (Historial de conversación)
            ArrayNode contents = requestBody.putArray("contents");

            for (Mensaje msg : conversacion.getMensajes()) {
                ObjectNode content = contents.addObject();
                // Mapear roles: 'assistant' -> 'model', 'user' -> 'user'
                String role = "assistant".equals(msg.getRol()) ? "model" : msg.getRol();
                content.put("role", role);

                ArrayNode parts = content.putArray("parts");
                parts.addObject().put("text", msg.getContenido());
            }

            // Configuración de generación (opcional)
            ObjectNode generationConfig = requestBody.putObject("generationConfig");
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 800);

            String url = geminiApiHost + "/v1beta/models/gemini-1.5-flash:generateContent?key=" + geminiApiKey;

            return webClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(response -> {
                        try {
                            return response.path("candidates")
                                    .get(0)
                                    .path("content")
                                    .path("parts")
                                    .get(0)
                                    .path("text")
                                    .asText();
                        } catch (Exception e) {
                            return "Error al procesar la respuesta de Gemini.";
                        }
                    })
                    .onErrorResume(error -> {
                        return Mono.just(
                                "Lo siento, hubo un error al conectar con el servicio de IA. Por favor intenta nuevamente.");
                    });

        } catch (Exception e) {
            return Mono.just("Error al procesar la solicitud: " + e.getMessage());
        }
    }

    public Conversacion obtenerConversacion(String conversacionId) {
        return conversaciones.get(conversacionId);
    }

    public void eliminarConversacion(String conversacionId) {
        conversaciones.remove(conversacionId);
    }

    public List<Mensaje> obtenerHistorial(String conversacionId) {
        Conversacion conversacion = conversaciones.get(conversacionId);
        return conversacion != null ? conversacion.getMensajes() : new ArrayList<>();
    }
}
