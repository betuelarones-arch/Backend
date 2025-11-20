package com.learning.cliente_app.ia.repository;

import com.learning.cliente_app.ia.model.AudioIaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para entidad AudioIa.
 * Proporciona operaciones CRUD y búsquedas de audios generados por IA.
 */
@Repository
public interface AudioIaRepository extends JpaRepository<AudioIaEntity, Long> {
}
