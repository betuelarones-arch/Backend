package com.learning.cliente_app.classroom.domain;

import jakarta.persistence.*;
import com.learning.cliente_app.user.model.UserEntity;
import java.time.LocalDateTime;

@Entity
@Table(name = "participaciones")
public class Participacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participacion")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_clase", nullable = false)
    private Clase clase;

    @ManyToOne
    @JoinColumn(name = "id_estudiante", nullable = false)
    private UserEntity usuario;

    @Column(name = "fecha_union", nullable = false)
    private LocalDateTime fechaUnion;

    public Participacion() {
        this.fechaUnion = LocalDateTime.now();
    }

    public Participacion(Clase clase, UserEntity usuario) {
        this.clase = clase;
        this.usuario = usuario;
        this.fechaUnion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Clase getClase() { return clase; }
    public void setClase(Clase clase) { this.clase = clase; }

    public UserEntity getUsuario() { return usuario; }
    public void setUsuario(UserEntity usuario) { this.usuario = usuario; }

    public LocalDateTime getFechaUnion() { return fechaUnion; }
    public void setFechaUnion(LocalDateTime fechaUnion) { this.fechaUnion = fechaUnion; }
}

