package com.learning.cliente_app.classroom.dto;

import java.time.LocalDateTime;

public class CrearClaseRequest {
    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    public CrearClaseRequest() {
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
}

