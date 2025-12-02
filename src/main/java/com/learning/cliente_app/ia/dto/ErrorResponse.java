package com.learning.cliente_app.ia.dto;

public class ErrorResponse {
    public String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() { return error; }
}