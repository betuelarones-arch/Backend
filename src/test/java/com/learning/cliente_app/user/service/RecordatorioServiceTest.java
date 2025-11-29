package com.learning.cliente_app.user.service;

import com.learning.cliente_app.user.dto.RecordatorioDTO;
import com.learning.cliente_app.user.model.RecordatorioEntity;
import com.learning.cliente_app.user.model.UserEntity;
import com.learning.cliente_app.user.repository.RecordatorioRepository;
import com.learning.cliente_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RecordatorioServiceTest {

    @Mock
    private RecordatorioRepository recordatorioRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecordatorioService recordatorioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void crear_ShouldCreateReminder() {
        Long userId = 1L;
        UserEntity user = new UserEntity();
        user.setId(userId);

        RecordatorioDTO dto = new RecordatorioDTO(null, "Test Reminder", LocalDateTime.now(), false);
        RecordatorioEntity entity = new RecordatorioEntity(user, dto.getTitulo(), dto.getFechaHora());
        entity.setId(1L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(recordatorioRepository.save(any(RecordatorioEntity.class))).thenReturn(entity);

        RecordatorioDTO result = recordatorioService.crear(userId, dto);

        assertNotNull(result.getId());
        assertEquals("Test Reminder", result.getTitulo());
    }

    @Test
    void listar_ShouldReturnList() {
        Long userId = 1L;
        when(recordatorioRepository.findByUsuarioIdOrderByFechaHoraAsc(userId))
                .thenReturn(List.of(new RecordatorioEntity()));

        List<RecordatorioDTO> result = recordatorioService.listarPorUsuario(userId);

        assertFalse(result.isEmpty());
    }
}
