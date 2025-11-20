package com.learning.cliente_app.lecciones.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String archivoOrigen;

    @Column(columnDefinition = "TEXT")
    private String pregunta;

    @Column(columnDefinition = "TEXT")
    private String opciones; // JSON String en BD

    private String respuestaCorrecta;

    @Transient
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Constructores
    public Pregunta() {}

    public Pregunta(String archivoOrigen, String pregunta, List<String> opciones, String respuestaCorrecta) {
        this.archivoOrigen = archivoOrigen;
        this.pregunta = pregunta;
        this.setOpcionesLista(opciones);
        this.respuestaCorrecta = respuestaCorrecta;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getArchivoOrigen() {
        return archivoOrigen;
    }

    public void setArchivoOrigen(String archivoOrigen) {
        this.archivoOrigen = archivoOrigen;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getOpciones() {
        return opciones;
    }

    public void setOpciones(String opciones) {
        this.opciones = opciones;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }

    // Métodos helper para trabajar con List
    @Transient
    public List<String> getOpcionesLista() {
        if (opciones == null || opciones.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(opciones, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al parsear opciones: " + e.getMessage(), e);
        }
    }

    @Transient
    public void setOpcionesLista(List<String> opcionesLista) {
        if (opcionesLista == null) {
            this.opciones = null;
            return;
        }
        try {
            this.opciones = objectMapper.writeValueAsString(opcionesLista);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al convertir opciones a JSON: " + e.getMessage(), e);
        }
    }
}