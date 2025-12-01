package com.learning.cliente_app.chatbot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learning.cliente_app.chatbot.dto.ChatResponse;
import com.learning.cliente_app.chatbot.model.Conversacion;
import com.learning.cliente_app.chatbot.model.Mensaje;
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

    public ChatbotService(WebClient openAiWebClient, ObjectMapper objectMapper) {
        this.webClient = openAiWebClient;
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

            // Model
            requestBody.put("model", "gpt-4o-mini");

            // Messages
            ArrayNode messages = requestBody.putArray("messages");

            // System prompt
            messages.addObject()
                    .put("role", "system")
                    .put("content", SYSTEM_PROMPT);

            // Conversation history
            for (Mensaje msg : conversacion.getMensajes()) {
                ObjectNode message = messages.addObject();
                // Map roles if needed, but we now use 'assistant' internally
                String role = "model".equals(msg.getRol()) ? "assistant" : msg.getRol();
                message.put("role", role);
                message.put("content", msg.getContenido());
            }

            // Configuration (optional)
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 800);

            // URL is already configured in WebClient
            return webClient.post()
                    .uri("/chat/completions") // Base URL is in config, so we just need the path? Or full URL?
                    // OpenAIConfig sets baseUrl to openai.api.url which is
                    // https://api.openai.com/v1/chat/completions
                    // Wait, if baseUrl is .../chat/completions, then uri should be empty or just
                    // query params.
                    // Let's check OpenAIConfig again.
                    // openai.api.url=https://api.openai.com/v1/chat/completions
                    // So baseUrl is the full endpoint.
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(response -> {
                        try {
                            return response.path("choices")
                                    .get(0)
                                    .path("message")
                                    .path("content")
                                    .asText();
                        } catch (Exception e) {
                            return "Error al procesar la respuesta de OpenAI.";
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

    public List<Conversacion> getRecentChats(Long userId) {
        if (userId == null)
            return new ArrayList<>();
        return conversaciones.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .sorted((c1, c2) -> c2.getLastActivity().compareTo(c1.getLastActivity()))
                .limit(5)
                .toList();
    }

    public List<Mensaje> obtenerHistorial(String conversacionId) {
        Conversacion conversacion = conversaciones.get(conversacionId);
        return conversacion != null ? conversacion.getMensajes() : new ArrayList<>();
    }

    public long countChatsByUserId(Long userId) {
        if (userId == null)
            return 0;
        return conversaciones.values().stream()
                .filter(c -> userId.equals(c.getUserId()))
                .count();
    }

    public String crearNuevaConversacion(Long userId) {
        String id = UUID.randomUUID().toString();
        Conversacion conversacion = new Conversacion(id);
        conversacion.setUserId(userId);
        conversaciones.put(id, conversacion);
        return id;
    }
}
