package com.learning.cliente_app.lecciones.repository;

import com.learning.cliente_app.lecciones.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByArchivoOrigen(String archivoOrigen);
    void deleteByArchivoOrigen(String archivoOrigen);
}

