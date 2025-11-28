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

    @Value("${openai.api.key}")
    private String openaiApiKey;

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
        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .build();
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

        String respuesta = obtenerRespuestaOpenAI(conversacion).block();

        conversacion.agregarMensaje(new Mensaje("assistant", respuesta));

        return new ChatResponse(conversacionId, respuesta, true, LocalDateTime.now());
    }

    private Mono<String> obtenerRespuestaOpenAI(Conversacion conversacion) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "gpt-4o-mini");
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 800);

            ArrayNode messages = requestBody.putArray("messages");

            // Mensaje del sistema
            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);

            // Historial de la conversación
            for (Mensaje msg : conversacion.getMensajes()) {
                ObjectNode message = messages.addObject();
                message.put("role", msg.getRol());
                message.put("content", msg.getContenido());
            }

            return webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + openaiApiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(response -> {
                        return response.path("choices")
                                .get(0)
                                .path("message")
                                .path("content")
                                .asText();
                    })
                    .onErrorResume(error -> {
                        return Mono
                                .just("Lo siento, hubo un error al procesar tu mensaje. Por favor intenta nuevamente.");
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
