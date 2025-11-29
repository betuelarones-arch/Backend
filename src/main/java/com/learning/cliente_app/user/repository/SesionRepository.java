package com.learning.cliente_app.user.repository;

import com.learning.cliente_app.user.model.SesionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar sesiones de usuarios.
 */
@Repository
public interface SesionRepository extends JpaRepository<SesionEntity, Long> {

    /**
     * Busca todas las sesiones activas de un usuario.
     */
    List<SesionEntity> findByIdUsuarioAndActivaTrue(Long idUsuario);

    /**
     * Busca una sesión activa por su hash de token.
     */
    Optional<SesionEntity> findByTokenHashAndActivaTrue(String tokenHash);

    /**
     * Elimina todas las sesiones de un usuario (útil al eliminar cuenta).
     */
    void deleteByIdUsuario(Long idUsuario);

    /**
     * Busca todas las sesiones de un usuario (activas o no).
     */
    List<SesionEntity> findByIdUsuario(Long idUsuario);
}
