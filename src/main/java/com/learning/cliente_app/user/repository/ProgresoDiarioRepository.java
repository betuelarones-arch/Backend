package com.learning.cliente_app.user.repository;

import com.learning.cliente_app.user.model.ProgresoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProgresoDiarioRepository extends JpaRepository<ProgresoDiario, Long> {

    Optional<ProgresoDiario> findByUsuarioIdAndFecha(Long usuarioId, LocalDate fecha);

    @Query("SELECT p FROM ProgresoDiario p WHERE p.usuario.id = :usuarioId AND p.fecha BETWEEN :startDate AND :endDate")
    List<ProgresoDiario> findByUsuarioIdAndFechaBetween(@Param("usuarioId") Long usuarioId,
            @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
