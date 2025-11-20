package com.learning.cliente_app.lecciones.repository;

import com.learning.cliente_app.lecciones.model.LeccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository para entidad Lección.
 * Proporciona operaciones CRUD y búsquedas.
 */
@Repository
public interface LeccionRepository extends JpaRepository<LeccionEntity, Long> {
}
