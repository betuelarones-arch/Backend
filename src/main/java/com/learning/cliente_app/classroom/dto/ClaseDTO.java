package com.learning.cliente_app.classroom.dto;

import java.time.LocalDateTime;

public class ClaseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String codigoUnico;
    private String urlUnirse;
    private byte[] qrCode;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private boolean activa;
    private Long profesorId;
    private String profesorNombre;
    private int cantidadEstudiantes;

    public ClaseDTO() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getUrlUnirse() { return urlUnirse; }
    public void setUrlUnirse(String urlUnirse) { this.urlUnirse = urlUnirse; }

    public byte[] getQrCode() { return qrCode; }
    public void setQrCode(byte[] qrCode) { this.qrCode = qrCode; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public Long getProfesorId() { return profesorId; }
    public void setProfesorId(Long profesorId) { this.profesorId = profesorId; }

    public String getProfesorNombre() { return profesorNombre; }
    public void setProfesorNombre(String profesorNombre) { this.profesorNombre = profesorNombre; }

    public int getCantidadEstudiantes() { return cantidadEstudiantes; }
    public void setCantidadEstudiantes(int cantidadEstudiantes) { this.cantidadEstudiantes = cantidadEstudiantes; }
}

