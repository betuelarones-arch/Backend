package com.learning.cliente_app.recursos.repository;

import com.learning.cliente_app.recursos.model.RecursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para entidad Recurso.
 * Proporciona operaciones CRUD y búsquedas de recursos.
 */
@Repository
public interface RecursoRepository extends JpaRepository<RecursoEntity, Long> {
}
