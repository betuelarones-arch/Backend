package com.learning.cliente_app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.learning.cliente_app.user.dto.ActualizarPerfilRequest;
import com.learning.cliente_app.user.dto.CambiarPasswordRequest;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private SesionService sesionService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UsuarioDTO registrarUsuario(UsuarioDTO usuario) {
        if (usuario == null || usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio para registrar el usuario");
        }

        Optional<UserEntity> existing = userRepository.findByEmail(usuario.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese email");
        }

        String hash = passwordEncoder.encode(usuario.getPassword() == null ? "" : usuario.getPassword());
        // apellido no está en el DTO actualmente, lo dejamos vacío; rol por defecto
        // 'estudiante'
        UserEntity entity = new UserEntity(usuario.getName(), "", usuario.getEmail(), hash, "estudiante", false);
        UserEntity saved = userRepository.save(entity);

        UsuarioDTO out = new UsuarioDTO();
        out.setId(saved.getId() != null ? saved.getId() : 0);
        out.setName(saved.getName());
        out.setEmail(saved.getEmail());
        out.setVerified(saved.isVerified());
        logger.info("Usuario registrado: {}", saved.getEmail());
        return out;
    }

    @Override
    public UsuarioDTO loginUsuario(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email y password son obligatorios para iniciar sesión");
        }

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        UsuarioDTO out = new UsuarioDTO();
        out.setId(user.getId() != null ? user.getId() : 0);
        out.setName(user.getName());
        out.setEmail(user.getEmail());
        out.setVerified(user.isVerified());
        logger.info("Login exitoso para: {}", email);
        return out;
    }

    @Override
    public void recuperarContrasena(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email es obligatorio para recuperar la contraseña");
        }

        try {
            String link = FirebaseAuth.getInstance().generatePasswordResetLink(email);
            logger.info("Generado link de recuperación para {} (token length={})", email,
                    link != null ? link.length() : 0);
            // Aquí: enviar correo con link usando servicio de emails (no implementado)
        } catch (FirebaseAuthException e) {
            logger.error("Error al generar enlace de recuperación para {}: {}", email, e.getMessage());
            throw new RuntimeException("No se pudo generar el enlace de recuperación: " + e.getMessage(), e);
        }
    }

    @Override
    public UsuarioDTO verificarUsuario(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token de verificación es obligatorio");
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
            String email = decodedToken.getEmail();
            UsuarioDTO usuario = new UsuarioDTO();
            usuario.setEmail(email);
            usuario.setName(Objects.toString(decodedToken.getName(), ""));
            usuario.setVerified(true);

            // Intentar marcar el usuario como verificado en DB si existe
            userRepository.findByEmail(email).ifPresent(u -> {
                u.setVerified(true);
                userRepository.save(u);
            });

            logger.info("Token verificado para: {}", usuario.getEmail());
            return usuario;
        } catch (Exception e) {
            logger.warn("Token inválido: {}", e.getMessage());
            throw new RuntimeException("Token inválido: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public UsuarioDTO actualizarPerfil(Long idUsuario, ActualizarPerfilRequest request) {
        UserEntity user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Actualizar campos solo si se proporcionan
        if (request.getNombre() != null && !request.getNombre().isBlank()) {
            user.setName(request.getNombre());
        }

        if (request.getApellido() != null && !request.getApellido().isBlank()) {
            user.setApellido(request.getApellido());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            // Verificar que el email no esté en uso por otro usuario
            if (!request.getEmail().equals(user.getEmail())) {
                Optional<UserEntity> existente = userRepository.findByEmail(request.getEmail());
                if (existente.isPresent()) {
                    throw new IllegalArgumentException("El email ya está en uso");
                }
                user.setEmail(request.getEmail());
            }
        }

        UserEntity updated = userRepository.save(user);
        logger.info("Perfil actualizado para usuario: {}", updated.getId());

        return convertirADTO(updated);
    }

    @Override
    @Transactional
    public UsuarioDTO subirFotoPerfil(Long idUsuario, MultipartFile foto) {
        UserEntity user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Eliminar foto anterior si existe
            if (user.getFotoPerfilUrl() != null && !user.getFotoPerfilUrl().isEmpty()) {
                fileStorageService.eliminarArchivo(user.getFotoPerfilUrl());
            }

            // Guardar nueva foto
            String fileName = fileStorageService.guardarArchivo(foto, idUsuario);
            user.setFotoPerfilUrl(fileName);

            UserEntity updated = userRepository.save(user);
            logger.info("Foto de perfil actualizada para usuario: {}", updated.getId());

            return convertirADTO(updated);
        } catch (IOException e) {
            logger.error("Error al guardar foto de perfil: {}", e.getMessage());
            throw new RuntimeException("Error al guardar la foto de perfil", e);
        }
    }

    @Override
    @Transactional
    public void cambiarPassword(Long idUsuario, CambiarPasswordRequest request) {
        UserEntity user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Validar que las contraseñas coincidan
        if (!request.getPasswordNueva().equals(request.getPasswordNuevaConfirm())) {
            throw new IllegalArgumentException("Las contraseñas nuevas no coinciden");
        }

        // Validar contraseña actual
        if (!passwordEncoder.matches(request.getPasswordActual(), user.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta");
        }

        // Validar que la nueva contraseña no sea vacía
        if (request.getPasswordNueva() == null || request.getPasswordNueva().length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Actualizar contraseña
        String nuevoHash = passwordEncoder.encode(request.getPasswordNueva());
        user.setPasswordHash(nuevoHash);
        userRepository.save(user);

        logger.info("Contraseña actualizada para usuario: {}", user.getId());
    }

    @Override
    @Transactional
    public void eliminarCuenta(Long idUsuario) {
        UserEntity user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            // Eliminar foto de perfil si existe
            if (user.getFotoPerfilUrl() != null && !user.getFotoPerfilUrl().isEmpty()) {
                fileStorageService.eliminarArchivo(user.getFotoPerfilUrl());
            }
        } catch (IOException e) {
            logger.warn("Error al eliminar foto de perfil durante eliminación de cuenta: {}", e.getMessage());
        }

        // Cerrar todas las sesiones
        sesionService.cerrarTodasLasSesiones(idUsuario);

        // Eliminar usuario
        userRepository.delete(user);
        logger.info("Cuenta eliminada para usuario: {}", idUsuario);
    }

    @Override
    public UsuarioDTO obtenerUsuarioPorId(Long idUsuario) {
        UserEntity user = userRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return convertirADTO(user);
    }

    /**
     * Método auxiliar para convertir UserEntity a UsuarioDTO.
     */
    private UsuarioDTO convertirADTO(UserEntity user) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(user.getId() != null ? user.getId() : 0);
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setVerified(user.isVerified());
        return dto;
    }
}
