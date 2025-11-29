package com.learning.cliente_app.user.service;

import com.learning.cliente_app.user.dto.ActualizarPerfilRequest;
import com.learning.cliente_app.user.dto.CambiarPasswordRequest;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    // Registro de usuario
    UsuarioDTO registrarUsuario(UsuarioDTO usuario);

    // Login de usuario
    UsuarioDTO loginUsuario(String email, String password);

    // Recuperación de contraseña
    void recuperarContrasena(String email);

    // Verificación de usuario vía token
    UsuarioDTO verificarUsuario(String token);

    // Actualizar perfil del usuario
    UsuarioDTO actualizarPerfil(Long idUsuario, ActualizarPerfilRequest request);

    // Subir foto de perfil
    UsuarioDTO subirFotoPerfil(Long idUsuario, MultipartFile foto);

    // Cambiar contraseña
    void cambiarPassword(Long idUsuario, CambiarPasswordRequest request);

    // Eliminar cuenta
    void eliminarCuenta(Long idUsuario);

    // Obtener usuario por ID
    UsuarioDTO obtenerUsuarioPorId(Long idUsuario);
}
