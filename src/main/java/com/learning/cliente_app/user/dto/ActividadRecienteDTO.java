package com.learning.cliente_app.user.dto;

import java.time.LocalDateTime;

public class ActividadRecienteDTO {
    private String id;
    private String tipo; // RESUMEN, EVALUACION, CHAT, CLASE
    private String titulo;
    private String descripcion;
    private LocalDateTime fecha;

    public ActividadRecienteDTO(String id, String tipo, String titulo, String descripcion, LocalDateTime fecha) {
        this.id = id;
        this.tipo = tipo;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
