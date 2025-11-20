package com.learning.cliente_app.classroom.service;

import com.learning.cliente_app.classroom.domain.*;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParticipacionService {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private ParticipacionRepository participacionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Un usuario se une a una clase usando el código único
     */
    @Transactional
    public Participacion unirseAClase(String codigoUnico, String emailUsuario) {
        // Buscar clase
        Clase clase = claseRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));

        if (!clase.isActiva()) {
            throw new RuntimeException("La clase no está activa");
        }

        // Buscar usuario
        UserEntity usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado. Debe registrarse primero."));

        // Verificar si ya está unido
        if (participacionRepository.existsByClase_IdAndUsuario_Id(clase.getId(), usuario.getId())) {
            throw new RuntimeException("Ya estás unido a esta clase");
        }

        // Crear participación
        Participacion participacion = new Participacion(clase, usuario);
        participacion = participacionRepository.save(participacion);

        return participacion;
    }
}

