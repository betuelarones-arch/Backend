package com.learning.cliente_app.classroom.domain;

import jakarta.persistence.*;
import com.learning.cliente_app.user.model.UserEntity;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clases")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_clase")
    private Long id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_creador", nullable = false)
    private UserEntity creador;

    @Column(name = "codigo_unico", nullable = false, unique = true, length = 20)
    private String codigoUnico;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    @OneToMany(mappedBy = "clase", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Participacion> participaciones = new HashSet<>();

    public Clase() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Clase(String nombre, String descripcion, UserEntity creador, String codigoUnico) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.creador = creador;
        this.codigoUnico = codigoUnico;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public UserEntity getCreador() { return creador; }
    public void setCreador(UserEntity creador) { this.creador = creador; }

    public String getCodigoUnico() { return codigoUnico; }
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public Set<Participacion> getParticipaciones() { return participaciones; }
    public void setParticipaciones(Set<Participacion> participaciones) { this.participaciones = participaciones; }
}

