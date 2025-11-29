package com.learning.cliente_app.user.service;

import com.learning.cliente_app.user.dto.SesionDTO;
import com.learning.cliente_app.user.model.SesionEntity;
import com.learning.cliente_app.user.repository.SesionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar sesiones de usuarios.
 * Permite logout real, gestión de sesiones remotas y tracking de dispositivos.
 */
@Service
public class SesionService {

    @Autowired
    private SesionRepository sesionRepository;

    /**
     * Crea una nueva sesión para un usuario.
     */
    @Transactional
    public SesionEntity crearSesion(Long idUsuario, String token, String dispositivo, String ip) {
        String tokenHash = hashToken(token);
        SesionEntity sesion = new SesionEntity(idUsuario, tokenHash, dispositivo, ip);
        return sesionRepository.save(sesion);
    }

    /**
     * Lista todas las sesiones activas de un usuario.
     */
    public List<SesionDTO> listarSesiones(Long idUsuario, String currentToken) {
        List<SesionEntity> sesiones = sesionRepository.findByIdUsuarioAndActivaTrue(idUsuario);
        String currentTokenHash = hashToken(currentToken);

        return sesiones.stream()
                .map(sesion -> convertirADTO(sesion, sesion.getTokenHash().equals(currentTokenHash)))
                .collect(Collectors.toList());
    }

    /**
     * Cierra la sesión actual (logout).
     */
    @Transactional
    public void cerrarSesion(String token) {
        String tokenHash = hashToken(token);
        sesionRepository.findByTokenHashAndActivaTrue(tokenHash).ifPresent(sesion -> {
            sesion.setActiva(false);
            sesionRepository.save(sesion);
        });
    }

    /**
     * Cierra una sesión remota específica.
     */
    @Transactional
    public void cerrarSesionRemota(Long idUsuario, Long idSesion) {
        SesionEntity sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new RuntimeException("Sesión no encontrada"));

        if (!sesion.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permiso para cerrar esta sesión");
        }

        sesion.setActiva(false);
        sesionRepository.save(sesion);
    }

    /**
     * Cierra todas las sesiones de un usuario (útil al eliminar cuenta).
     */
    @Transactional
    public void cerrarTodasLasSesiones(Long idUsuario) {
        List<SesionEntity> sesiones = sesionRepository.findByIdUsuario(idUsuario);
        sesiones.forEach(sesion -> sesion.setActiva(false));
        sesionRepository.saveAll(sesiones);
    }

    /**
     * Actualiza el timestamp de última actividad.
     */
    @Transactional
    public void actualizarActividad(String token) {
        String tokenHash = hashToken(token);
        sesionRepository.findByTokenHashAndActivaTrue(tokenHash).ifPresent(sesion -> {
            sesion.setUltimaActividad(OffsetDateTime.now());
            sesionRepository.save(sesion);
        });
    }

    /**
     * Verifica si una sesión es válida (existe y está activa).
     */
    public boolean esSesionValida(String token) {
        String tokenHash = hashToken(token);
        return sesionRepository.findByTokenHashAndActivaTrue(tokenHash).isPresent();
    }

    /**
     * Genera un hash del token para almacenamiento seguro.
     */
    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al hashear token", e);
        }
    }

    /**
     * Convierte una entidad a DTO.
     */
    private SesionDTO convertirADTO(SesionEntity sesion, boolean esActual) {
        return new SesionDTO(
                sesion.getId(),
                sesion.getDispositivo(),
                sesion.getIpAddress(),
                sesion.getFechaCreacion(),
                sesion.getUltimaActividad(),
                esActual);
    }
}
