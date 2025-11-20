package com.learning.cliente_app.lecciones.dto;

import java.util.List;

public class PreguntaUpdateDTO
{
    private String pregunta;
    private List<String> opciones;
    private String respuestaCorrecta;

    // Constructores
    public PreguntaUpdateDTO() {}

    public PreguntaUpdateDTO(String pregunta, List<String> opciones, String respuestaCorrecta) {
        this.pregunta = pregunta;
        this.opciones = opciones;
        this.respuestaCorrecta = respuestaCorrecta;
    }

    // Getters y Setters
    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public List<String> getOpciones() {
        return opciones;
    }

    public void setOpciones(List<String> opciones) {
        this.opciones = opciones;
    }

    public String getRespuestaCorrecta() {
        return respuestaCorrecta;
    }

    public void setRespuestaCorrecta(String respuestaCorrecta) {
        this.respuestaCorrecta = respuestaCorrecta;
    }
}
