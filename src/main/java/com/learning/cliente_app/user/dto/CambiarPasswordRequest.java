package com.learning.cliente_app.user.dto;

/**
 * DTO para cambiar la contraseña del usuario.
 * Requiere la contraseña actual para verificación y la nueva contraseña con
 * confirmación.
 */
public class CambiarPasswordRequest {

    private String passwordActual;
    private String passwordNueva;
    private String passwordNuevaConfirm;

    public CambiarPasswordRequest() {
    }

    public CambiarPasswordRequest(String passwordActual, String passwordNueva, String passwordNuevaConfirm) {
        this.passwordActual = passwordActual;
        this.passwordNueva = passwordNueva;
        this.passwordNuevaConfirm = passwordNuevaConfirm;
    }

    public String getPasswordActual() {
        return passwordActual;
    }

    public void setPasswordActual(String passwordActual) {
        this.passwordActual = passwordActual;
    }

    public String getPasswordNueva() {
        return passwordNueva;
    }

    public void setPasswordNueva(String passwordNueva) {
        this.passwordNueva = passwordNueva;
    }

    public String getPasswordNuevaConfirm() {
        return passwordNuevaConfirm;
    }

    public void setPasswordNuevaConfirm(String passwordNuevaConfirm) {
        this.passwordNuevaConfirm = passwordNuevaConfirm;
    }
}
