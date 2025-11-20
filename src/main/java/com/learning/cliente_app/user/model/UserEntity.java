package com.learning.cliente_app.user.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "usuarios")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String name;

    @Column(name = "apellido", nullable = false)
    private String apellido;

    @Column(name = "correo", nullable = false, unique = true)
    private String email;

    // stored as bcrypt hash
    @Column(name = "contrasena", nullable = false)
    private String passwordHash;

    @Column(name = "rol", nullable = false)
    private String rol;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @Column(name = "fecha_creacion")
    private OffsetDateTime fechaCreacion;

    public UserEntity() {
    }

    public UserEntity(String name, String apellido, String email, String passwordHash, String rol, boolean verified) {
        this.name = name;
        this.apellido = apellido == null ? "" : apellido;
        this.email = email;
        this.passwordHash = passwordHash;
        this.rol = rol == null ? "estudiante" : rol;
        this.verified = verified;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getApellido() { return apellido; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public boolean isVerified() { return verified; }
    public String getRol() { return rol; }
    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setRol(String rol) { this.rol = rol; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
