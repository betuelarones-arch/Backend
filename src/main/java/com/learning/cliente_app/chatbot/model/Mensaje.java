package com.learning.cliente_app.chatbot.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Mensaje individual
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {
    private String rol; // "user" o "assistant"
    private String contenido;
    private LocalDateTime timestamp;
    
    public Mensaje(String rol, String contenido) {
        this.rol = rol;
        this.contenido = contenido;
        this.timestamp = LocalDateTime.now();
    }
}



