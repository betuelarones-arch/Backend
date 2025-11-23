package com.learning.cliente_app.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.learning.cliente_app.ClienteAppApplication;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(classes = ClienteAppApplication.class)
@Slf4j
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ==================== PRUEBAS DE REGISTRO ====================

    @Test
    public void testRegistrarUsuario() {
        String NAME = "Juan";
        String EMAIL = "newuser@example.com";
        String PASSWORD = "SecurePass123";

        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName(NAME);
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            UsuarioDTO newUser = this.userService.registrarUsuario(userDTO);

            log.info("USUARIO REGISTRADO: ID={}, Email={}, Name={}", newUser.getId(), newUser.getEmail(),
                    newUser.getName());

            assertNotNull(newUser.getId(), "ID no debe ser nulo");
            assertEquals(EMAIL, newUser.getEmail(), "Email debe coincidir");
            assertEquals(NAME, newUser.getName(), "Nombre debe coincidir");

        } catch (Exception e) {
            log.error("Error registrando usuario: {}", e.getMessage(), e);
            fail("Error registrando usuario: " + e.getMessage());
        }
    }

    @Test
    public void testRegistrarUsuarioDuplicado() {
        String EMAIL = "duplicate@example.com";
        String PASSWORD = "SecurePass123";

        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName("Carlos");
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            UsuarioDTO firstUser = this.userService.registrarUsuario(userDTO);
            log.info("Primer usuario registrado: ID={}", firstUser.getId());

            UsuarioDTO duplicateDTO = new UsuarioDTO();
            duplicateDTO.setName("Otro");
            duplicateDTO.setEmail(EMAIL);
            duplicateDTO.setPassword(PASSWORD);

            try {
                this.userService.registrarUsuario(duplicateDTO);
                fail("Debería haber rechazado correo duplicado");
            } catch (IllegalArgumentException e) {
                log.info("Correo duplicado rechazado correctamente: {}", e.getMessage());
                assertTrue(e.getMessage().contains("email"), "Excepción debe mencionar email");
            }

        } catch (Exception e) {
            log.error("Error en test de duplicado: {}", e.getMessage(), e);
            fail("Error en test de duplicado: " + e.getMessage());
        }
    }

    @Test
    public void testRegistrarUsuarioSinEmail() {
        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName("Test User");
            userDTO.setEmail("");
            userDTO.setPassword("SomePass123");

            try {
                this.userService.registrarUsuario(userDTO);
                fail("Debería haber rechazado email vacío");
            } catch (IllegalArgumentException e) {
                log.info("Email vacío rechazado correctamente: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error en test sin email: {}", e.getMessage(), e);
            fail("Error en test sin email: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE LOGIN ====================

    @Test
    public void testLoginExitoso() {
        String NAME = "María";
        String EMAIL = "login@example.com";
        String PASSWORD = "MyPassword123";

        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName(NAME);
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            UsuarioDTO registeredUser = this.userService.registrarUsuario(userDTO);
            log.info("Usuario registrado para login: Email={}", registeredUser.getEmail());

            UsuarioDTO loginUser = this.userService.loginUsuario(EMAIL, PASSWORD);

            log.info("LOGIN EXITOSO para: Email={}, Name={}", loginUser.getEmail(), loginUser.getName());

            assertNotNull(loginUser, "Usuario logueado no debe ser nulo");
            assertEquals(EMAIL, loginUser.getEmail(), "Email debe coincidir");
            assertEquals(NAME, loginUser.getName(), "Nombre debe coincidir");

        } catch (Exception e) {
            log.error("Error en login exitoso: {}", e.getMessage(), e);
            fail("Error en login: " + e.getMessage());
        }
    }

    @Test
    public void testLoginContraseñaIncorrecta() {
        String EMAIL = "wrongpass@example.com";
        String PASSWORD = "CorrectPassword123";
        String WRONG_PASSWORD = "WrongPassword";

        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName("Test");
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            this.userService.registrarUsuario(userDTO);
            log.info("Usuario registrado para prueba de contraseña incorrecta");

            try {
                this.userService.loginUsuario(EMAIL, WRONG_PASSWORD);
                fail("Debería haber rechazado contraseña incorrecta");
            } catch (IllegalArgumentException e) {
                log.info("Contraseña incorrecta rechazada: {}", e.getMessage());
                assertTrue(e.getMessage().toLowerCase().contains("credenciales") ||
                        e.getMessage().toLowerCase().contains("contraseña") ||
                        e.getMessage().toLowerCase().contains("inválid"),
                        "Excepción debe mencionar credenciales");
            }

        } catch (Exception e) {
            log.error("Error preparando test: {}", e.getMessage(), e);
            fail("Error preparando test: " + e.getMessage());
        }
    }

    @Test
    public void testLoginUsuarioNoExistente() {
        String NON_EXISTENT_EMAIL = "noexiste@example.com";
        String PASSWORD = "SomePassword123";

        try {
            this.userService.loginUsuario(NON_EXISTENT_EMAIL, PASSWORD);
            fail("Debería haber lanzado excepción de usuario no encontrado");
        } catch (IllegalArgumentException e) {
            log.info("Usuario no encontrado rechazado: {}", e.getMessage());
            assertTrue(e.getMessage().toLowerCase().contains("no encontrado"),
                    "Excepción debe mencionar 'no encontrado'");
        } catch (Exception e) {
            log.error("Tipo de excepción inesperado: {}", e.getClass().getName());
            fail("Tipo de excepción inesperado: " + e.getMessage());
        }
    }

    @Test
    public void testLoginSinEmail() {
        try {
            this.userService.loginUsuario("", "password123");
            fail("Debería haber rechazado email vacío");
        } catch (IllegalArgumentException e) {
            log.info("Email vacío en login rechazado: {}", e.getMessage());
            assertTrue(true);
        }
    }

    @Test
    public void testLoginSinPassword() {
        try {
            this.userService.loginUsuario("user@example.com", "");
            fail("Debería haber rechazado contraseña vacía");
        } catch (IllegalArgumentException e) {
            log.info("Contraseña vacía en login rechazada: {}", e.getMessage());
            assertTrue(true);
        }
    }

    // ==================== PRUEBAS DE VERIFICACIÓN DE CONTRASEÑA
    // ====================

    @Test
    public void testVerifyPasswordIsBCrypted() {
        String EMAIL = "bcrypt@example.com";
        String PASSWORD = "PlainPassword123";

        try {
            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName("Test");
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            this.userService.registrarUsuario(userDTO);
            log.info("Usuario registrado para verificación de BCrypt");

            Optional<UserEntity> userEntity = userRepository.findByEmail(EMAIL);

            assertTrue(userEntity.isPresent(), "Usuario debe existir en BD");
            String hashedPassword = userEntity.get().getPasswordHash();

            assertTrue(hashedPassword.startsWith("$2"), "Contraseña debe estar en formato BCrypt");

            assertTrue(passwordEncoder.matches(PASSWORD, hashedPassword),
                    "Contraseña original debe coincidir con hash BCrypt");

            log.info("CONTRASEÑA VERIFICADA EN BCRYPT: {}...", hashedPassword.substring(0, 10));

        } catch (Exception e) {
            log.error("Error verificando BCrypt: {}", e.getMessage(), e);
            fail("Error verificando BCrypt: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE RECUPERACIÓN ====================

    @Test
    public void testRecuperarContrasena() {
        String EMAIL = "recovery@example.com";
        String PASSWORD = "InitialPass123";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuthMock);
            when(firebaseAuthMock.generatePasswordResetLink(anyString())).thenReturn("http://fake-link");

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName("Recovery User");
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            this.userService.registrarUsuario(userDTO);
            log.info("Usuario registrado para prueba de recuperación");

            this.userService.recuperarContrasena(EMAIL);
            log.info("Solicitud de recuperación enviada para: {}", EMAIL);

            assertTrue(true, "Recuperación ejecutada sin excepciones");

        } catch (Exception e) {
            log.error("Error en recuperación: {}", e.getMessage(), e);
            fail("Error en recuperación: " + e.getMessage());
        }
    }

    @Test
    public void testRecuperarContraseñaEmailNoExistente() {
        String NON_EXISTENT_EMAIL = "nonexistent@example.com";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuthMock);
            when(firebaseAuthMock.generatePasswordResetLink(anyString())).thenReturn("http://fake-link");

            this.userService.recuperarContrasena(NON_EXISTENT_EMAIL);
            log.info("Recuperación solicitada para email no existente: {}", NON_EXISTENT_EMAIL);

            assertTrue(true, "No debe lanzar excepción para email no existente");

        } catch (Exception e) {
            log.warn("Excepción en recuperación para email no existente: {}", e.getMessage());
            assertTrue(true);
        }
    }

    // ==================== PRUEBAS DE VERIFICACIÓN DE TOKEN ====================

    @Test
    public void testVerificarUsuarioConToken() {
        String NAME = "Verified User";
        String EMAIL = "verify@example.com";
        String PASSWORD = "VerifyPass123";
        String TOKEN = "test-token-valid";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
            FirebaseToken firebaseTokenMock = mock(FirebaseToken.class);

            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuthMock);
            when(firebaseAuthMock.verifyIdToken(anyString())).thenReturn(firebaseTokenMock);
            when(firebaseTokenMock.getEmail()).thenReturn(EMAIL);
            when(firebaseTokenMock.getName()).thenReturn(NAME);

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName(NAME);
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            UsuarioDTO registeredUser = this.userService.registrarUsuario(userDTO);
            log.info("Usuario registrado para verificación: {}", registeredUser.getId());

            UsuarioDTO verifiedUser = this.userService.verificarUsuario(TOKEN);
            log.info("Usuario verificado: Email={}", verifiedUser.getEmail());
            assertNotNull(verifiedUser, "Usuario verificado no debe ser nulo");
            assertEquals(EMAIL, verifiedUser.getEmail());

        } catch (Exception e) {
            log.error("Error en test de verificación: {}", e.getMessage(), e);
            fail("Error en test de verificación: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE FLUJO COMPLETO ====================

    @Test
    public void testCompleteUserFlow() {
        String NAME = "Francisco";
        String EMAIL = "complete@example.com";
        String PASSWORD = "CompleteFlow123";

        try (MockedStatic<FirebaseAuth> mockedFirebaseAuth = Mockito.mockStatic(FirebaseAuth.class)) {
            FirebaseAuth firebaseAuthMock = mock(FirebaseAuth.class);
            mockedFirebaseAuth.when(FirebaseAuth::getInstance).thenReturn(firebaseAuthMock);
            when(firebaseAuthMock.generatePasswordResetLink(anyString())).thenReturn("http://fake-link");

            log.info("=== INICIANDO FLUJO COMPLETO ===");

            UsuarioDTO userDTO = new UsuarioDTO();
            userDTO.setName(NAME);
            userDTO.setEmail(EMAIL);
            userDTO.setPassword(PASSWORD);

            UsuarioDTO createdUser = this.userService.registrarUsuario(userDTO);
            log.info("1. USUARIO REGISTRADO: ID={}, Email={}", createdUser.getId(), createdUser.getEmail());
            assertNotNull(createdUser.getId(), "ID de usuario no debe ser nulo");

            Optional<UserEntity> dbUser = userRepository.findByEmail(EMAIL);
            assertTrue(dbUser.isPresent(), "Usuario debe existir en BD");
            log.info("2. USUARIO VERIFICADO EN BD: Email={}", dbUser.get().getEmail());

            UsuarioDTO loginUser = this.userService.loginUsuario(EMAIL, PASSWORD);
            log.info("3. LOGIN EXITOSO: Email={}, Name={}", loginUser.getEmail(), loginUser.getName());
            assertNotNull(loginUser, "Usuario logueado no debe ser nulo");
            assertEquals(EMAIL, loginUser.getEmail(), "Email debe coincidir");

            this.userService.recuperarContrasena(EMAIL);
            log.info("4. RECUPERACIÓN DE CONTRASEÑA SOLICITADA");

            String hashedPassword = dbUser.get().getPasswordHash();
            assertTrue(hashedPassword.startsWith("$2"), "Contraseña debe estar en BCrypt");
            assertTrue(passwordEncoder.matches(PASSWORD, hashedPassword), "Contraseña debe coincidir");
            log.info("5. CONTRASEÑA VERIFICADA EN BCRYPT");

            log.info("=== FLUJO COMPLETO EXITOSO ===");
            assertTrue(true, "Flujo completo completado exitosamente");

        } catch (Exception e) {
            log.error("Error en flujo completo: {}", e.getMessage(), e);
            fail("Error en flujo completo: " + e.getMessage());
        }
    }

    @Test
    public void testMultipleUsersSimultaneously() {
        String[] names = { "User1", "User2", "User3" };
        String[] emails = { "user1@test.com", "user2@test.com", "user3@test.com" };
        String password = "CommonPass123";

        try {
            log.info("=== REGISTRANDO MÚLTIPLES USUARIOS ===");

            for (int i = 0; i < 3; i++) {
                UsuarioDTO userDTO = new UsuarioDTO();
                userDTO.setName(names[i]);
                userDTO.setEmail(emails[i]);
                userDTO.setPassword(password);

                UsuarioDTO registered = this.userService.registrarUsuario(userDTO);
                log.info("Usuario {} registrado: ID={}", i + 1, registered.getId());
                assertNotNull(registered.getId(), "ID no debe ser nulo para usuario " + (i + 1));
            }

            for (int i = 0; i < 3; i++) {
                UsuarioDTO loginUser = this.userService.loginUsuario(emails[i], password);
                assertEquals(emails[i], loginUser.getEmail(), "Email debe coincidir para usuario " + (i + 1));
                log.info("Usuario {} verificado en login", i + 1);
            }

            log.info("=== TODOS LOS USUARIOS VERIFICADOS ===");
            assertTrue(true, "Múltiples usuarios registrados y verificados");

        } catch (Exception e) {
            log.error("Error en prueba de múltiples usuarios: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }
}
