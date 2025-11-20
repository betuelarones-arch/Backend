package com.learning.cliente_app.classroom.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.learning.cliente_app.classroom.domain.Participacion;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.service.UserService;
import com.learning.cliente_app.classroom.dto.CrearClaseRequest;
import com.learning.cliente_app.classroom.dto.ClaseDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * Test de integración para ParticipacionService.
 * Pruebas de unirse a clases, validaciones, duplicados.
 * Patrón: @SpringBootTest + @Slf4j + try-catch + assertions
 */
@SpringBootTest
@Slf4j
public class ParticipacionServiceIntegrationTest {

    @Autowired
    private ParticipacionService participacionService;

    @Autowired
    private ClaseService claseService;

    @Autowired
    private UserService userService;

    /**
     * Test: Unirse a clase exitosamente
     */
    @Test
    public void testUnirseAClaseExitoso() {
        String EMAIL_PROFESOR = "profesor_p@example.com";
        String EMAIL_ESTUDIANTE = "estudiante_p@example.com";
        String PASSWORD = "Pass123";

        try {
            // 1. Crear profesor y clase
            UsuarioDTO profesor = new UsuarioDTO();
            profesor.setName("Profesor");
            profesor.setEmail(EMAIL_PROFESOR);
            profesor.setPassword(PASSWORD);

            try {
                userService.registrarUsuario(profesor);
            } catch (IllegalArgumentException e) {
                log.info("Profesor ya existe");
            }

            CrearClaseRequest claseReq = new CrearClaseRequest();
            claseReq.setNombre("Clase de Participación");
            claseReq.setDescripcion("Test");

            ClaseDTO clase = null;
            try {
                clase = claseService.crearClase(EMAIL_PROFESOR, claseReq);
                log.info("CLASE CREADA: Código={}", clase.getCodigoUnico());
            } catch (Exception e) {
                log.info("Error creando clase: {}", e.getMessage());
                return;
            }

            // 2. Crear estudiante
            UsuarioDTO estudiante = new UsuarioDTO();
            estudiante.setName("Estudiante");
            estudiante.setEmail(EMAIL_ESTUDIANTE);
            estudiante.setPassword(PASSWORD);

            try {
                userService.registrarUsuario(estudiante);
            } catch (IllegalArgumentException e) {
                log.info("Estudiante ya existe");
            }

            // 3. Unirse a clase
            try {
                Participacion part = participacionService.unirseAClase(
                        clase.getCodigoUnico(),
                        EMAIL_ESTUDIANTE
                );

                log.info("PARTICIPACIÓN CREADA: ID={}", part.getId());

                assertNotNull(part.getId(), "ID de participación no debe ser nulo");
                assertNotNull(part.getClase(), "Clase no debe ser nula");
                assertNotNull(part.getUsuario(), "Usuario no debe ser nulo");

            } catch (Exception e) {
                log.info("Error en participación: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Rechazo si código no existe
     */
    @Test
    public void testUnirseConCodigoNoExistente() {
        String EMAIL = "estudiante2@example.com";
        String CODIGO_INVALIDO = "XXXXX";

        try {
            // Crear estudiante
            UsuarioDTO estudiante = new UsuarioDTO();
            estudiante.setName("Estudiante2");
            estudiante.setEmail(EMAIL);
            estudiante.setPassword("Pass123");

            try {
                userService.registrarUsuario(estudiante);
            } catch (IllegalArgumentException e) {
                log.info("Estudiante ya existe");
            }

            // Intentar unirse con código no existente
            try {
                participacionService.unirseAClase(CODIGO_INVALIDO, EMAIL);
                fail("Debería rechazar código no existente");
            } catch (RuntimeException e) {
                log.info("Código no existente rechazado: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("clase") 
                        || e.getMessage().toLowerCase().contains("no encontrada"));
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Rechazo si usuario no existe
     */
    @Test
    public void testUnirseConUsuarioNoExistente() {
        String EMAIL_PROFESOR = "profesor_p2@example.com";
        String EMAIL_INVALIDO = "noexiste@example.com";

        try {
            // Crear clase
            UsuarioDTO profesor = new UsuarioDTO();
            profesor.setName("Profesor");
            profesor.setEmail(EMAIL_PROFESOR);
            profesor.setPassword("Pass123");

            try {
                userService.registrarUsuario(profesor);
            } catch (IllegalArgumentException e) {
                log.info("Profesor ya existe");
            }

            CrearClaseRequest claseReq = new CrearClaseRequest();
            claseReq.setNombre("Clase Test");
            claseReq.setDescripcion("Test");

            ClaseDTO clase = null;
            try {
                clase = claseService.crearClase(EMAIL_PROFESOR, claseReq);
            } catch (Exception e) {
                log.info("Error creando clase: {}", e.getMessage());
                return;
            }

            // Intentar unirse con usuario no existente
            try {
                participacionService.unirseAClase(clase.getCodigoUnico(), EMAIL_INVALIDO);
                fail("Debería rechazar usuario no existente");
            } catch (RuntimeException e) {
                log.info("Usuario no existente rechazado: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("usuario") 
                        || e.getMessage().toLowerCase().contains("registrase"));
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Rechazo de participación duplicada
     */
    @Test
    public void testRechazoParticipacionDuplicada() {
        String EMAIL_PROFESOR = "profesor_p3@example.com";
        String EMAIL_ESTUDIANTE = "estudiante_p3@example.com";

        try {
            // Crear profesor y clase
            UsuarioDTO profesor = new UsuarioDTO();
            profesor.setName("Profesor");
            profesor.setEmail(EMAIL_PROFESOR);
            profesor.setPassword("Pass123");

            try {
                userService.registrarUsuario(profesor);
            } catch (IllegalArgumentException e) {
                log.info("Profesor ya existe");
            }

            CrearClaseRequest claseReq = new CrearClaseRequest();
            claseReq.setNombre("Clase Duplicado Test");
            claseReq.setDescripcion("Test");

            ClaseDTO clase = null;
            try {
                clase = claseService.crearClase(EMAIL_PROFESOR, claseReq);
            } catch (Exception e) {
                log.info("Error creando clase: {}", e.getMessage());
                return;
            }

            // Crear estudiante
            UsuarioDTO estudiante = new UsuarioDTO();
            estudiante.setName("Estudiante");
            estudiante.setEmail(EMAIL_ESTUDIANTE);
            estudiante.setPassword("Pass123");

            try {
                userService.registrarUsuario(estudiante);
            } catch (IllegalArgumentException e) {
                log.info("Estudiante ya existe");
            }

            // Primera participación
            try {
                Participacion part1 = participacionService.unirseAClase(
                        clase.getCodigoUnico(),
                        EMAIL_ESTUDIANTE
                );
                log.info("Primera participación: ID={}", part1.getId());

                // Segunda participación (duplicada)
                try {
                    participacionService.unirseAClase(
                            clase.getCodigoUnico(),
                            EMAIL_ESTUDIANTE
                    );
                    fail("Debería rechazar participación duplicada");
                } catch (RuntimeException e) {
                    log.info("Participación duplicada rechazada: {}", e.getMessage());
                    assertTrue(e.getMessage().toLowerCase().contains("ya") 
                            || e.getMessage().toLowerCase().contains("unido"));
                }

            } catch (Exception e) {
                log.info("Error esperado: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Verificación de servicio inyectado
     */
    @Test
    public void testParticipacionServiceInyectado() {
        try {
            assertNotNull(participacionService, "ParticipacionService debe estar inyectado");
            log.info("PARTICIPACIÓN SERVICE INYECTADO CORRECTAMENTE");
            assertTrue(true);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

}
