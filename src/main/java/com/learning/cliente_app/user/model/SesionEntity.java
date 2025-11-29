package com.learning.cliente_app.user.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

/**
 * Entidad para gestionar sesiones activas de usuarios.
 * Permite implementar logout real y gestión de sesiones remotas.
 */
@Entity
@Table(name = "sesiones")
public class SesionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "token_hash", nullable = false, length = 500)
    private String tokenHash;

    @Column(name = "dispositivo", length = 500)
    private String dispositivo;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "fecha_creacion", nullable = false)
    private OffsetDateTime fechaCreacion;

    @Column(name = "ultima_actividad", nullable = false)
    private OffsetDateTime ultimaActividad;

    @Column(name = "activa", nullable = false)
    private boolean activa;

    public SesionEntity() {
        this.fechaCreacion = OffsetDateTime.now();
        this.ultimaActividad = OffsetDateTime.now();
        this.activa = true;
    }

    public SesionEntity(Long idUsuario, String tokenHash, String dispositivo, String ipAddress) {
        this();
        this.idUsuario = idUsuario;
        this.tokenHash = tokenHash;
        this.dispositivo = dispositivo;
        this.ipAddress = ipAddress;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
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

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }
}
