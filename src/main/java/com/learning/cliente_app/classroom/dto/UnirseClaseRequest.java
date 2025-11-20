package com.learning.cliente_app.classroom.dto;

public class UnirseClaseRequest {
    private String codigoUnico;
    private String emailEstudiante; // Email del usuario que se quiere unir

    public UnirseClaseRequest() {
    }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public String getEmailEstudiante() { return emailEstudiante; }
    public void setEmailEstudiante(String emailEstudiante) { this.emailEstudiante = emailEstudiante; }
}

