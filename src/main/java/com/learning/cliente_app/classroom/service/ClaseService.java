package com.learning.cliente_app.classroom.service;

import com.learning.cliente_app.classroom.domain.*;
import com.learning.cliente_app.classroom.dto.ClaseDTO;
import com.learning.cliente_app.classroom.dto.CrearClaseRequest;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClaseService {

    @Autowired
    private ClaseRepository claseRepository;

    @Autowired
    private CodigoUnicoService codigoUnicoService;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crea una nueva clase para un usuario
     */
    @Transactional
    public ClaseDTO crearClase(String emailUsuario, CrearClaseRequest request) throws IOException {
        // Buscar usuario
        UserEntity usuario = userRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Generar código único
        String codigoUnico;
        do {
            codigoUnico = codigoUnicoService.generarCodigoCorto();
        } while (claseRepository.existsByCodigoUnico(codigoUnico));

        // Crear clase
        Clase clase = new Clase();
        clase.setNombre(request.getNombre());
        clase.setDescripcion(request.getDescripcion());
        clase.setCreador(usuario);
        clase.setCodigoUnico(codigoUnico);
        clase.setFechaInicio(request.getFechaInicio() != null ? request.getFechaInicio() : LocalDateTime.now());
        clase.setFechaFin(request.getFechaFin());
        clase.setActiva(true);

        clase = claseRepository.save(clase);

        // Convertir a DTO con QR code
        return convertirADTO(clase, true);
    }

    /**
     * Obtiene todas las clases creadas por un usuario
     */
    public List<ClaseDTO> obtenerClasesPorUsuario(String emailUsuario) {
        List<Clase> clases = claseRepository.findByCreador_Email(emailUsuario);
        return clases.stream()
                .map(clase -> convertirADTO(clase, false))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una clase por código único
     */
    public ClaseDTO obtenerClasePorCodigo(String codigoUnico) {
        Clase clase = claseRepository.findByCodigoUnico(codigoUnico)
                .orElseThrow(() -> new RuntimeException("Clase no encontrada"));
        return convertirADTO(clase, false);
    }

    /**
     * Convierte una entidad Clase a DTO
     */
    private ClaseDTO convertirADTO(Clase clase, boolean incluirQR) {
        ClaseDTO dto = new ClaseDTO();
        dto.setId(clase.getId());
        dto.setNombre(clase.getNombre());
        dto.setDescripcion(clase.getDescripcion());
        dto.setCodigoUnico(clase.getCodigoUnico());
        dto.setFechaCreacion(clase.getFechaCreacion());
        dto.setFechaInicio(clase.getFechaInicio());
        dto.setFechaFin(clase.getFechaFin());
        dto.setActiva(clase.isActiva());
        dto.setProfesorId(clase.getCreador().getId());
        dto.setProfesorNombre(clase.getCreador().getName());
        dto.setCantidadEstudiantes(clase.getParticipaciones().size());

        // Generar URL y QR code
        try {
            String urlUnirse = qrCodeService.generarURLUnirse(clase.getCodigoUnico());
            dto.setUrlUnirse(urlUnirse);

            if (incluirQR) {
                byte[] qrCode = qrCodeService.generarQRCodeParaClase(clase.getCodigoUnico());
                dto.setQrCode(qrCode);
            }
        } catch (Exception e) {
            // Si falla la generación del QR, continuar sin él
            dto.setUrlUnirse(qrCodeService.generarURLUnirse(clase.getCodigoUnico()));
        }

        return dto;
    }
}
