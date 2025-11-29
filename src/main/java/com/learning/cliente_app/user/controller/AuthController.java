package com.learning.cliente_app.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.learning.cliente_app.user.dto.CambiarPasswordRequest;
import com.learning.cliente_app.user.dto.SesionDTO;
import com.learning.cliente_app.user.service.UserService;
import com.learning.cliente_app.user.service.SesionService;

import java.util.List;

/**
 * Controlador para endpoints de autenticación y gestión de sesiones.
 * Incluye cambio de contraseña, listado de sesiones, logout y cierre de
 * sesiones remotas.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private SesionService sesionService;

    /**
     * Cambiar contraseña del usuario.
     * POST /api/auth/cambiar-password
     * Header: userId (simulando autenticación)
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<String> cambiarPassword(
            @RequestHeader("userId") Long userId,
            @RequestBody CambiarPasswordRequest request) {
        try {
            userService.cambiarPassword(userId, request);
            return ResponseEntity.ok("Contraseña cambiada exitosamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al cambiar contraseña: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cambiar la contraseña");
        }
    }

    /**
     * Listar sesiones activas del usuario.
     * GET /api/auth/sesiones
     * Header: userId (simulando autenticación)
     * Header: Authorization (para identificar sesión actual)
     */
    @GetMapping("/sesiones")
    public ResponseEntity<List<SesionDTO>> listarSesiones(
            @RequestHeader("userId") Long userId,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // Extraer token del header "Bearer TOKEN"
            String actualToken = token != null && token.startsWith("Bearer ")
                    ? token.substring(7)
                    : "";

            List<SesionDTO> sesiones = sesionService.listarSesiones(userId, actualToken);
            return ResponseEntity.ok(sesiones);
        } catch (Exception e) {
            logger.error("Error al listar sesiones: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cerrar sesión actual (logout real).
     * POST /api/auth/logout
     * Header: Authorization (token a invalidar)
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.badRequest().body("Token inválido");
            }

            String token = authHeader.substring(7);
            sesionService.cerrarSesion(token);
            return ResponseEntity.ok("Sesión cerrada exitosamente");
        } catch (Exception e) {
            logger.error("Error al cerrar sesión: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cerrar la sesión");
        }
    }

    /**
     * Cerrar sesión remota específica.
     * DELETE /api/auth/sesiones/{id}
     * Header: userId (simulando autenticación)
     */
    @DeleteMapping("/sesiones/{id}")
    public ResponseEntity<String> cerrarSesionRemota(
            @RequestHeader("userId") Long userId,
            @PathVariable Long id) {
        try {
            sesionService.cerrarSesionRemota(userId, id);
            return ResponseEntity.ok("Sesión remota cerrada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error al cerrar sesión remota: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cerrar la sesión remota");
        }
    }
}
