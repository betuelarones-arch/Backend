package com.learning.cliente_app.classroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClaseRepository extends JpaRepository<Clase, Long> {
    Optional<Clase> findByCodigoUnico(String codigoUnico);
    List<Clase> findByCreador_Id(Long creadorId);
    List<Clase> findByCreador_Email(String email);
    boolean existsByCodigoUnico(String codigoUnico);
}

