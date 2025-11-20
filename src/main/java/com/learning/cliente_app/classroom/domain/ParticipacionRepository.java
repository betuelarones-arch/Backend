package com.learning.cliente_app.classroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParticipacionRepository extends JpaRepository<Participacion, Long> {
    Optional<Participacion> findByClase_IdAndUsuario_Id(Long claseId, Long usuarioId);
    List<Participacion> findByClase_Id(Long claseId);
    List<Participacion> findByUsuario_Id(Long usuarioId);
    boolean existsByClase_IdAndUsuario_Id(Long claseId, Long usuarioId);
}

