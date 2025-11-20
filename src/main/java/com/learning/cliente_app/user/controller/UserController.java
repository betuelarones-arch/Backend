package com.learning.cliente_app.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.learning.cliente_app.user.service.UserService;
import com.learning.cliente_app.user.dto.UsuarioDTO;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

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
    public ResponseEntity<FirebaseLoginResponse> firebaseLogin(@RequestHeader("Authorization") String authorizationHeader) throws Exception {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        String idToken = authorizationHeader.replace("Bearer ", "").trim();
        com.google.firebase.auth.FirebaseToken decodedToken = com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        String email = decodedToken.getEmail();
        FirebaseLoginResponse resp = new FirebaseLoginResponse(uid, email);
        return ResponseEntity.ok(resp);
    }

    // Small request/response DTOs for controller endpoints
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RecoverRequest {
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    public static class FirebaseLoginResponse {
        private String uid;
        private String email;

        public FirebaseLoginResponse(String uid, String email) {
            this.uid = uid;
            this.email = email;
        }

        public String getUid() { return uid; }
        public String getEmail() { return email; }
    }

}
