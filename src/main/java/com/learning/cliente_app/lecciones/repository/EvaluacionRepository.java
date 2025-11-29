package com.learning.cliente_app.lecciones.repository;

import com.learning.cliente_app.lecciones.model.EvaluacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluacionRepository extends JpaRepository<EvaluacionEntity, Long> {
    long countByUsuarioId(Long usuarioId);

    List<EvaluacionEntity> findTop5ByUsuarioIdOrderByFechaRealizacionDesc(Long usuarioId);
}
