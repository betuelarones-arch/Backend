package com.learning.cliente_app.chatbot.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conversacion {
    private String id;
    private List<Mensaje> mensajes = new ArrayList<>();
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime lastActivity = LocalDateTime.now();
    private Long userId;

    public Conversacion(String id) {
        this.id = id;
        this.mensajes = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }

    public void agregarMensaje(Mensaje mensaje) {
        this.mensajes.add(mensaje);
        this.lastActivity = LocalDateTime.now();
    }
}