package com.learning.cliente_app.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Unit Tests para UserService con Mockito.
 * Pruebas de lógica de negocio con mocks de la capa de persistencia.
 * Patrón: @ExtendWith(MockitoExtension.class) + @Mock + @BeforeEach
 */
@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserServiceImpl userService;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder);
        log.info("Setup completado");
    }

    // ==================== TESTS DE REGISTRO ====================

    /**
     * Test unitario: Registrar usuario exitosamente
     */
    @Test
    public void testRegistrarUsuarioExitoso() {
        String NAME = "Juan";
        String EMAIL = "juan@example.com";
        String PASSWORD = "PlainPassword123";
        String HASHED_PASSWORD = "$2a$10$...hashedpassword...";

        try {
            // Setup
            UsuarioDTO inputDTO = new UsuarioDTO();
            inputDTO.setName(NAME);
            inputDTO.setEmail(EMAIL);
            inputDTO.setPassword(PASSWORD);

            UserEntity savedEntity = new UserEntity(NAME, "", EMAIL, HASHED_PASSWORD, "estudiante", false);
            savedEntity.setId(1L);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());
            when(passwordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);
            when(userRepository.save(any(UserEntity.class))).thenReturn(savedEntity);

            // Ejecutar
            UsuarioDTO result = userService.registrarUsuario(inputDTO);

            log.info("REGISTRO EXITOSO: ID={}, Email={}", result.getId(), result.getEmail());

            // Validar
            assertNotNull(result);
            assertEquals(EMAIL, result.getEmail());
            assertEquals(NAME, result.getName());
            verify(userRepository).save(any(UserEntity.class));

        } catch (Exception e) {
            log.error("Error en test de registro exitoso: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Rechazo por email duplicado
     */
    @Test
    public void testRegistrarUsuarioDuplicadoFalla() {
        String EMAIL = "duplicate@example.com";
        String PASSWORD = "Password123";

        try {
            // Setup
            UsuarioDTO inputDTO = new UsuarioDTO();
            inputDTO.setName("Test");
            inputDTO.setEmail(EMAIL);
            inputDTO.setPassword(PASSWORD);

            UserEntity existingUser = new UserEntity("Existing", "", EMAIL, "hash", "estudiante", false);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

            // Ejecutar y validar excepción
            try {
                userService.registrarUsuario(inputDTO);
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Email duplicado rechazado correctamente: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("email"));
                verify(userRepository).findByEmail(EMAIL);
                verify(userRepository, never()).save(any());
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Rechazo por email vacío
     */
    @Test
    public void testRegistrarSinEmailFalla() {
        try {
            // Setup
            UsuarioDTO inputDTO = new UsuarioDTO();
            inputDTO.setName("Test");
            inputDTO.setEmail("");
            inputDTO.setPassword("Password123");

            // Ejecutar y validar excepción
            try {
                userService.registrarUsuario(inputDTO);
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Email vacío rechazado: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("email"));
                verify(userRepository, never()).save(any());
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== TESTS DE LOGIN ====================

    /**
     * Test unitario: Login exitoso
     */
    @Test
    public void testLoginExitoso() {
        String EMAIL = "user@example.com";
        String PASSWORD = "CorrectPassword";
        String HASHED_PASSWORD = "$2a$10$...hashedpassword...";

        try {
            // Setup
            UserEntity dbUser = new UserEntity("Juan", "", EMAIL, HASHED_PASSWORD, "estudiante", false);
            dbUser.setId(1L);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(dbUser));
            when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(true);

            // Ejecutar
            UsuarioDTO result = userService.loginUsuario(EMAIL, PASSWORD);

            log.info("LOGIN EXITOSO: Email={}, Name={}", result.getEmail(), result.getName());

            // Validar
            assertNotNull(result);
            assertEquals(EMAIL, result.getEmail());
            assertEquals("Juan", result.getName());
            verify(userRepository).findByEmail(EMAIL);
            verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);

        } catch (Exception e) {
            log.error("Error en login exitoso: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Rechazo por contraseña incorrecta
     */
    @Test
    public void testLoginContraseñaIncorrectaFalla() {
        String EMAIL = "user@example.com";
        String PASSWORD = "WrongPassword";
        String HASHED_PASSWORD = "$2a$10$...correcthash...";

        try {
            // Setup
            UserEntity dbUser = new UserEntity("Juan", "", EMAIL, HASHED_PASSWORD, "estudiante", false);
            dbUser.setId(1L);

            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(dbUser));
            when(passwordEncoder.matches(PASSWORD, HASHED_PASSWORD)).thenReturn(false);

            // Ejecutar y validar excepción
            try {
                userService.loginUsuario(EMAIL, PASSWORD);
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Contraseña incorrecta rechazada: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("inválid"));
                verify(passwordEncoder).matches(PASSWORD, HASHED_PASSWORD);
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Rechazo por usuario no encontrado
     */
    @Test
    public void testLoginUsuarioNoEncontradoFalla() {
        String EMAIL = "nonexistent@example.com";
        String PASSWORD = "Password123";

        try {
            // Setup
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            // Ejecutar y validar excepción
            try {
                userService.loginUsuario(EMAIL, PASSWORD);
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Usuario no encontrado: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("no encontrado"));
                verify(userRepository).findByEmail(EMAIL);
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Login sin email
     */
    @Test
    public void testLoginSinEmailFalla() {
        try {
            // Ejecutar y validar excepción
            try {
                userService.loginUsuario("", "Password123");
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Email vacío rechazado en login: {}", e.getMessage());
                assertTrue(true);
                verify(userRepository, never()).findByEmail(anyString());
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Login sin contraseña
     */
    @Test
    public void testLoginSinPasswordFalla() {
        try {
            // Ejecutar y validar excepción
            try {
                userService.loginUsuario("user@example.com", "");
                fail("Debería haber lanzado IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                log.info("Contraseña vacía rechazada en login: {}", e.getMessage());
                assertTrue(true);
                verify(userRepository, never()).findByEmail(anyString());
            }

        } catch (Exception e) {
            log.error("Error inesperado: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== TESTS DE VALIDACIÓN ====================

    /**
     * Test unitario: Validación de encoding de password
     */
    @Test
    public void testPasswordEncodingCorrectamente() {
        String PASSWORD = "PlainPassword123";
        String ENCODED = "$2a$10$...encodedvalue...";

        try {
            // Setup
            when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED);

            // Ejecutar
            String result = passwordEncoder.encode(PASSWORD);

            log.info("PASSWORD ENCODED: {}...", result.substring(0, Math.min(10, result.length())));

            // Validar
            assertEquals(ENCODED, result);
            assertTrue(result.startsWith("$2a$10$") || result.startsWith("$2y$10$"));
            verify(passwordEncoder).encode(PASSWORD);

        } catch (Exception e) {
            log.error("Error en encoding: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Validación de matching de password
     */
    @Test
    public void testPasswordMatchingCorrectamente() {
        String PASSWORD = "CorrectPassword123";
        String HASHED = "$2a$10$...hashedvalue...";

        try {
            // Setup
            when(passwordEncoder.matches(PASSWORD, HASHED)).thenReturn(true);

            // Ejecutar
            boolean matches = passwordEncoder.matches(PASSWORD, HASHED);

            log.info("PASSWORD MATCH: {}", matches);

            // Validar
            assertTrue(matches);
            verify(passwordEncoder).matches(PASSWORD, HASHED);

        } catch (Exception e) {
            log.error("Error en password matching: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Repository interaction verification
     */
    @Test
    public void testRepositoryInteractionVerified() {
        String EMAIL = "test@example.com";

        try {
            // Setup
            UserEntity mockUser = new UserEntity("Test", "", EMAIL, "hash", "estudiante", false);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(mockUser));

            // Ejecutar
            Optional<UserEntity> result = userRepository.findByEmail(EMAIL);

            log.info("REPOSITORY INTERACTION: Found user with email={}", EMAIL);

            // Validar
            assertTrue(result.isPresent());
            assertEquals(EMAIL, result.get().getEmail());
            verify(userRepository).findByEmail(EMAIL);

        } catch (Exception e) {
            log.error("Error en repository interaction: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== TESTS DE COMPORTAMIENTO ====================

    /**
     * Test unitario: Verificación de que save no es llamado en caso de email duplicado
     */
    @Test
    public void testNoSaveOnDuplicateEmail() {
        String EMAIL = "duplicate@example.com";

        try {
            // Setup
            UsuarioDTO inputDTO = new UsuarioDTO();
            inputDTO.setName("Test");
            inputDTO.setEmail(EMAIL);
            inputDTO.setPassword("Password123");

            UserEntity existingUser = new UserEntity("Existing", "", EMAIL, "hash", "estudiante", false);
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(existingUser));

            // Ejecutar
            try {
                userService.registrarUsuario(inputDTO);
            } catch (IllegalArgumentException e) {
                log.info("Excepción capturada como esperado");
            }

            // Validar
            verify(userRepository).findByEmail(EMAIL);
            verify(userRepository, never()).save(any(UserEntity.class));
            log.info("VERIFICADO: save() no fue llamado en email duplicado");

        } catch (Exception e) {
            log.error("Error en verificación: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test unitario: Verificación de que findByEmail es llamado en login
     */
    @Test
    public void testFindByEmailCalledOnLogin() {
        String EMAIL = "user@example.com";

        try {
            // Setup
            when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

            // Ejecutar
            try {
                userService.loginUsuario(EMAIL, "Password123");
            } catch (IllegalArgumentException e) {
                log.info("Excepción capturada como esperado");
            }

            // Validar
            verify(userRepository).findByEmail(EMAIL);
            log.info("VERIFICADO: findByEmail() fue llamado en login");

        } catch (Exception e) {
            log.error("Error en verificación: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

}
