package com.learning.cliente_app.resumen.repository;

import com.learning.cliente_app.resumen.model.ResumenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResumenRepository extends JpaRepository<ResumenEntity, Long> {
    long countByUsuarioId(Long usuarioId);

    List<ResumenEntity> findTop5ByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId);
}
