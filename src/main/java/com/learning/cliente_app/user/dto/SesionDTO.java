package com.learning.cliente_app.user.dto;

import java.time.OffsetDateTime;

/**
 * DTO para representar una sesión activa del usuario.
 * Incluye información del dispositivo, IP y timestamps.
 */
public class SesionDTO {

    private Long id;
    private String dispositivo;
    private String ipAddress;
    private OffsetDateTime fechaCreacion;
    private OffsetDateTime ultimaActividad;
    private boolean esActual;

    public SesionDTO() {
    }

    public SesionDTO(Long id, String dispositivo, String ipAddress,
            OffsetDateTime fechaCreacion, OffsetDateTime ultimaActividad, boolean esActual) {
        this.id = id;
        this.dispositivo = dispositivo;
        this.ipAddress = ipAddress;
        this.fechaCreacion = fechaCreacion;
        this.ultimaActividad = ultimaActividad;
        this.esActual = esActual;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public OffsetDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(OffsetDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public boolean isEsActual() {
        return esActual;
    }

    public void setEsActual(boolean esActual) {
        this.esActual = esActual;
    }
}
