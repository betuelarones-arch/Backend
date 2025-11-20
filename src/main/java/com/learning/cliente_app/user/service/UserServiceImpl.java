package com.learning.cliente_app.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.learning.cliente_app.user.dto.UsuarioDTO;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        // apellido no está en el DTO actualmente, lo dejamos vacío; rol por defecto 'estudiante'
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

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
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
            logger.info("Generado link de recuperación para {} (token length={})", email, link != null ? link.length() : 0);
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
}
