package com.learning.cliente_app.chatbot.controller;

import com.learning.cliente_app.chatbot.dto.ChatRequest;
import com.learning.cliente_app.chatbot.dto.ChatResponse;
import com.learning.cliente_app.chatbot.model.Mensaje;
import com.learning.cliente_app.chatbot.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = { "*", "*" })
public class ChatbotController {

    private final ChatbotService chatbotService;

    /**
     * POST /api/chat/nueva-conversacion
     * Crea una nueva conversación con contexto limpio
     */
    @PostMapping("/nueva-conversacion")
    public ResponseEntity<Map<String, String>> crearConversacion() {
        String conversacionId = chatbotService.crearNuevaConversacion();
        Map<String, String> response = new HashMap<>();
        response.put("conversacionId", conversacionId);
        response.put("mensaje", "Nueva conversación creada exitosamente");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/chat/mensaje
     * Envía un mensaje y obtiene respuesta del chatbot
     * Body: { "conversacionId": "uuid", "mensaje": "tu pregunta" }
     */
    @PostMapping("/mensaje")
    public ResponseEntity<?> enviarMensaje(@Valid @RequestBody ChatRequest request) {
        if (request.getMensaje() == null || request.getMensaje().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El mensaje no puede estar vacío");
            return ResponseEntity.badRequest().body(error);
        }

        if (request.getConversacionId() == null || request.getConversacionId().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Se requiere un ID de conversación válido");
            return ResponseEntity.badRequest().body(error);
        }

        try {
            ChatResponse response = chatbotService.procesarMensaje(
                    request.getConversacionId(),
                    request.getMensaje());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al procesar el mensaje: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/chat/historial/{conversacionId}
     * Obtiene el historial completo de una conversación
     */
    @GetMapping("/historial/{conversacionId}")
    public ResponseEntity<?> obtenerHistorial(@PathVariable String conversacionId) {
        try {
            List<Mensaje> historial = chatbotService.obtenerHistorial(conversacionId);
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Conversación no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * DELETE /api/chat/conversacion/{conversacionId}
     * Elimina una conversación y libera memoria
     */
    @DeleteMapping("/conversacion/{conversacionId}")
    public ResponseEntity<Map<String, String>> eliminarConversacion(@PathVariable String conversacionId) {
        chatbotService.eliminarConversacion(conversacionId);
        Map<String, String> response = new HashMap<>();
        response.put("mensaje", "Conversación eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/chat/salud
     * Health check del servicio
     */
    @GetMapping("/salud")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("estado", "activo");
        response.put("servicio", "Chatbot Educativo");
        return ResponseEntity.ok(response);
    }
}