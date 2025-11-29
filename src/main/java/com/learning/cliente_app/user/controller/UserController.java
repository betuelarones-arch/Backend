package com.learning.cliente_app.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.learning.cliente_app.user.service.UserService;
import com.learning.cliente_app.user.dto.ActualizarPerfilRequest;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.dto.LoginRequest;
import com.learning.cliente_app.user.dto.RecoverRequest;
import com.learning.cliente_app.user.dto.FirebaseLoginResponse;
import com.learning.cliente_app.user.dto.SupportRequest;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Registro de usuario
    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> registerUser(@RequestBody UsuarioDTO usuario) {
        UsuarioDTO nuevoUsuario = userService.registrarUsuario(usuario);
        // En creación suele devolver 201 CREATED
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // Login de usuario
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> loginUser(@RequestBody LoginRequest login) {
        UsuarioDTO usuarioLogueado = userService.loginUsuario(login.getEmail(), login.getPassword());
        return ResponseEntity.ok(usuarioLogueado);
    }

    // Recuperación de contraseña
    @PostMapping("/recover")
    public ResponseEntity<String> recoverPassword(@RequestBody RecoverRequest req) {
        userService.recuperarContrasena(req.getEmail());
        return ResponseEntity.ok("Correo de recuperación enviado correctamente a " + req.getEmail());
    }

    // Verificación de usuario vía token
    @GetMapping("/verify")
    public ResponseEntity<UsuarioDTO> verifyUser(@RequestParam String token) {
        UsuarioDTO usuarioVerificado = userService.verificarUsuario(token);
        return ResponseEntity.ok(usuarioVerificado);
    }

    // Endpoint para login con Firebase con correo electrónico
    @PostMapping("/firebase-login")
    public ResponseEntity<FirebaseLoginResponse> firebaseLogin(
            @RequestHeader("Authorization") String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        com.google.firebase.auth.FirebaseToken decodedToken = com.google.firebase.auth.FirebaseAuth.getInstance()
                .verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        FirebaseLoginResponse resp = new FirebaseLoginResponse(uid, email);
        return ResponseEntity.ok(resp);
    }

    // Reportar error o soporte
    @PostMapping("/support")
    public ResponseEntity<String> reportError(@RequestBody SupportRequest request) {
        logger.info("Support message received: {}", request.getDescription());
        // Aquí podrías almacenar el mensaje o enviarlo por email
        return ResponseEntity.ok("Mensaje de soporte recibido");
    }

    /**
     * Actualizar perfil del usuario.
     * PUT /api/usuarios/perfil
     * Header: userId (simulando autenticación)
     */
    @PutMapping("/perfil")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(
            @RequestHeader("userId") Long userId,
            @RequestBody ActualizarPerfilRequest request) {
        try {
            UsuarioDTO actualizado = userService.actualizarPerfil(userId, request);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error al actualizar perfil: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Subir foto de perfil.
     * POST /api/usuarios/foto (multipart)
     * Header: userId (simulando autenticación)
     */
    @PostMapping("/foto")
    public ResponseEntity<UsuarioDTO> subirFoto(
            @RequestHeader("userId") Long userId,
            @RequestParam("file") MultipartFile file) {
        try {
            UsuarioDTO actualizado = userService.subirFotoPerfil(userId, file);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error al subir foto: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Eliminar cuenta del usuario.
     * DELETE /api/usuarios/cuenta
     * Header: userId (simulando autenticación)
     */
    @DeleteMapping("/cuenta")
    public ResponseEntity<String> eliminarCuenta(@RequestHeader("userId") Long userId) {
        try {
            userService.eliminarCuenta(userId);
            return ResponseEntity.ok("Cuenta eliminada exitosamente");
        } catch (Exception e) {
            logger.error("Error al eliminar cuenta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
