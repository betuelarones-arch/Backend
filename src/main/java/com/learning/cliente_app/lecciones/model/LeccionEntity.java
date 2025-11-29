package com.learning.cliente_app.lecciones.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "lecciones")
public class LeccionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_leccion")
    private Long id;

    @Column(name = "id_curso", nullable = false)
    private Long idCurso;

    @Column(name = "titulo", nullable = false)
    private String titulo;

    @Column(name = "resumen")
    private String resumen;

    @Column(name = "url_ppt")
    private String urlPpt;

    @Column(name = "ppt_blob", columnDefinition = "bytea")
    private byte[] pptBlob;

    @Column(name = "url_video")
    private String urlVideo;

    @Column(name = "numero")
    private Integer numero;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    // getters and setters
    public Long getId() {
        return id;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public String getUrlPpt() {
        return urlPpt;
    }

    public byte[] getPptBlob() {
        return pptBlob;
    }

    public String getUrlVideo() {
        return urlVideo;
    }

    public Integer getNumero() {
        return numero;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public void setUrlPpt(String urlPpt) {
        this.urlPpt = urlPpt;
    }

    public void setPptBlob(byte[] pptBlob) {
        this.pptBlob = pptBlob;
    }

    public void setUrlVideo(String urlVideo) {
        this.urlVideo = urlVideo;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
