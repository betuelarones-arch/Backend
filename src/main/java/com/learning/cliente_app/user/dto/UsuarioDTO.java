package com.learning.cliente_app.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO simple para comunicación entre backend y frontend.
 * - El campo {@code password} se acepta en requests pero no se devuelve en responses.
 * - El campo {@code token} no se expone en responses.
 */
public class UsuarioDTO {
    private long id;
    private String name;
    private String email;

    // Permitimos recibir el password en requests, pero marcamos WRITE_ONLY para que
    // no se incluya en las respuestas JSON.
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private boolean verified;

    @JsonIgnore
    private String token;

    public UsuarioDTO() {

    }

    public UsuarioDTO(long id, String name, String email, String password, boolean verified, String token) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.verified = verified;
        this.token = token;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getToken() {
        return token;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
