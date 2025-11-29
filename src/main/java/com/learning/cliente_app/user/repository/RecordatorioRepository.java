package com.learning.cliente_app.user.repository;

import com.learning.cliente_app.user.model.RecordatorioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordatorioRepository extends JpaRepository<RecordatorioEntity, Long> {
    List<RecordatorioEntity> findByUsuarioIdOrderByFechaHoraAsc(Long usuarioId);
}
