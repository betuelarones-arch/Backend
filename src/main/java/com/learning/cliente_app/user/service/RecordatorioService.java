package com.learning.cliente_app.user.service;

import com.learning.cliente_app.user.dto.RecordatorioDTO;
import com.learning.cliente_app.user.model.RecordatorioEntity;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.RecordatorioRepository;
import com.learning.cliente_app.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecordatorioService {

    private final RecordatorioRepository recordatorioRepository;
    private final UserRepository userRepository;

    public RecordatorioService(RecordatorioRepository recordatorioRepository, UserRepository userRepository) {
        this.recordatorioRepository = recordatorioRepository;
        this.userRepository = userRepository;
    }

    public List<RecordatorioDTO> listarPorUsuario(Long usuarioId) {
        return recordatorioRepository.findByUsuarioIdOrderByFechaHoraAsc(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RecordatorioDTO crear(Long usuarioId, RecordatorioDTO dto) {
        UserEntity usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        RecordatorioEntity entity = new RecordatorioEntity(usuario, dto.getTitulo(), dto.getFechaHora());
        RecordatorioEntity saved = recordatorioRepository.save(entity);
        return mapToDTO(saved);
    }

    public void eliminar(Long id) {
        recordatorioRepository.deleteById(id);
    }

    public RecordatorioDTO toggleCompletado(Long id) {
        RecordatorioEntity entity = recordatorioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recordatorio no encontrado"));

        entity.setCompletado(!entity.isCompletado());
        RecordatorioEntity saved = recordatorioRepository.save(entity);
        return mapToDTO(saved);
    }

    private RecordatorioDTO mapToDTO(RecordatorioEntity entity) {
        return new RecordatorioDTO(
                entity.getId(),
                entity.getTitulo(),
                entity.getFechaHora(),
                entity.isCompletado());
    }
}
