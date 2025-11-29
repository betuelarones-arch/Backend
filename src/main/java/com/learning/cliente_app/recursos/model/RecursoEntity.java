package com.learning.cliente_app.recursos.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recursos")
public class RecursoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso")
    private Long id;

    @Column(name = "id_leccion", nullable = false)
    private Long idLeccion;

    @Column(name = "id_diapositiva")
    private Long idDiapositiva;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "url")
    private String url;

    @Column(name = "metadata")
    private String metadata;

    @Column(name = "file_blob", columnDefinition = "bytea")
    private byte[] fileBlob;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    // getters/setters
    public Long getId() {
        return id;
    }

    public Long getIdLeccion() {
        return idLeccion;
    }

    public Long getIdDiapositiva() {
        return idDiapositiva;
    }

    public String getTipo() {
        return tipo;
    }

    public String getUrl() {
        return url;
    }

    public String getMetadata() {
        return metadata;
    }

    public byte[] getFileBlob() {
        return fileBlob;
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

    public void setIdDiapositiva(Long idDiapositiva) {
        this.idDiapositiva = idDiapositiva;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public void setFileBlob(byte[] fileBlob) {
        this.fileBlob = fileBlob;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
