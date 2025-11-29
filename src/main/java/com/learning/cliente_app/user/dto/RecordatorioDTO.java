package com.learning.cliente_app.user.dto;

import java.time.LocalDateTime;

public class RecordatorioDTO {
    private Long id;
    private String titulo;
    private LocalDateTime fechaHora;
    private boolean completado;

    public RecordatorioDTO() {
    }

    public RecordatorioDTO(Long id, String titulo, LocalDateTime fechaHora, boolean completado) {
        this.id = id;
        this.titulo = titulo;
        this.fechaHora = fechaHora;
        this.completado = completado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}
