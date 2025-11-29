package com.learning.cliente_app.user.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "progreso_diario")
public class ProgresoDiario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private UserEntity usuario;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    @Column(name = "minutos_estudio", nullable = false)
    private Integer minutosEstudio;

    public ProgresoDiario() {
    }

    public ProgresoDiario(UserEntity usuario, LocalDate fecha, Integer minutosEstudio) {
        this.usuario = usuario;
        this.fecha = fecha;
        this.minutosEstudio = minutosEstudio;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getMinutosEstudio() {
        return minutosEstudio;
    }

    public void setMinutosEstudio(Integer minutosEstudio) {
        this.minutosEstudio = minutosEstudio;
    }
}
