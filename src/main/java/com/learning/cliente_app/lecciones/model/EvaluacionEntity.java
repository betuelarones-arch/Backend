package com.learning.cliente_app.lecciones.model;

import com.learning.cliente_app.user.model.UserEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "evaluaciones")
public class EvaluacionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity usuario;

    @ManyToOne
    @JoinColumn(name = "id_leccion", nullable = false)
    private LeccionEntity leccion;

    @Column(name = "nota", nullable = false)
    private Double nota;

    @Column(name = "fecha_realizacion")
    private OffsetDateTime fechaRealizacion;

    public EvaluacionEntity() {
        this.fechaRealizacion = OffsetDateTime.now();
    }

    public EvaluacionEntity(UserEntity usuario, LeccionEntity leccion, Double nota) {
        this.usuario = usuario;
        this.leccion = leccion;
        this.nota = nota;
        this.fechaRealizacion = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserEntity getUsuario() {
        return usuario;
    }

    public void setUsuario(UserEntity usuario) {
        this.usuario = usuario;
    }

    public LeccionEntity getLeccion() {
        return leccion;
    }

    public void setLeccion(LeccionEntity leccion) {
        this.leccion = leccion;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public OffsetDateTime getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(OffsetDateTime fechaRealizacion) {
        this.fechaRealizacion = fechaRealizacion;
    }
}
