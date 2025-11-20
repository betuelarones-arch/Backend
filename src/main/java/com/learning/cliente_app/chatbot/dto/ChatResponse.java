package com.learning.cliente_app.chatbot.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String conversacionId;
    private String respuesta;
    private LocalDateTime timestamp;
    
    public ChatResponse(String conversacionId, String respuesta, boolean b, LocalDateTime now) {
        this.conversacionId = conversacionId;
        this.respuesta = respuesta;
        this.timestamp = LocalDateTime.now();
    }
}