package com.learning.cliente_app.user.dto;

/**
 * DTO para actualizar el perfil del usuario.
 * Todos los campos son opcionales, solo se actualizarán los que se envíen.
 */
public class ActualizarPerfilRequest {

    private String nombre;
    private String apellido;
    private String email;

    public ActualizarPerfilRequest() {
    }

    public ActualizarPerfilRequest(String nombre, String apellido, String email) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
