package com.learning.cliente_app.user.service;

import com.learning.cliente_app.user.dto.UsuarioDTO;

public interface UserService {
     // Registro de usuario
    UsuarioDTO registrarUsuario(UsuarioDTO usuario);

    // Login de usuario
    UsuarioDTO loginUsuario(String email, String password);

    // Recuperación de contraseña
    void recuperarContrasena(String email);

    // Verificación de usuario vía token
    UsuarioDTO verificarUsuario(String token);
}
