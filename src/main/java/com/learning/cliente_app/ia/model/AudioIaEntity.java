package com.learning.cliente_app.ia.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "audios_ia")
public class AudioIaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_audio")
    private Long id;

    @Column(name = "id_leccion")
    private Long idLeccion;

    @Column(name = "texto_input")
    private String textoInput;

    @Column(name = "url_audio")
    private String urlAudio;

    @Column(name = "audio_blob", columnDefinition = "bytea")
    private byte[] audioBlob;

    @Column(name = "modelo")
    private String modelo;

    @Column(name = "duracion_seg")
    private Double duracionSeg;

    @Column(name = "costo_usd")
    private Double costoUsd;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    // getters/setters
    public Long getId() {
        return id;
    }

    public Long getIdLeccion() {
        return idLeccion;
    }

    public String getTextoInput() {
        return textoInput;
    }

    public String getUrlAudio() {
        return urlAudio;
    }

    public byte[] getAudioBlob() {
        return audioBlob;
    }

    public String getModelo() {
        return modelo;
    }

    public Double getDuracionSeg() {
        return duracionSeg;
    }

    public Double getCostoUsd() {
        return costoUsd;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdLeccion(Long idLeccion) {
        this.idLeccion = idLeccion;
    }

    public void setTextoInput(String textoInput) {
        this.textoInput = textoInput;
    }

    public void setUrlAudio(String urlAudio) {
        this.urlAudio = urlAudio;
    }

    public void setAudioBlob(byte[] audioBlob) {
        this.audioBlob = audioBlob;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setDuracionSeg(Double duracionSeg) {
        this.duracionSeg = duracionSeg;
    }

    public void setCostoUsd(Double costoUsd) {
        this.costoUsd = costoUsd;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
