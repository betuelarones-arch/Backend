package com.learning.cliente_app.user.service;

import com.learning.cliente_app.chatbot.service.ChatbotService;
import com.learning.cliente_app.lecciones.repository.EvaluacionRepository;
import com.learning.cliente_app.resumen.repository.ResumenRepository;
import com.learning.cliente_app.user.dto.DashboardDTO;
import com.learning.cliente_app.user.model.ProgresoDiario;
import com.learning.cliente_app.user.repository.ProgresoDiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Mock
    private ProgresoDiarioRepository progresoRepository;

    @Mock
    private ResumenRepository resumenRepository;

    @Mock
    private EvaluacionRepository evaluacionRepository;

    @Mock
    private ChatbotService chatbotService;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDashboardData_ShouldReturnCorrectData() {
        Long userId = 1L;

        // Mock Progreso
        ProgresoDiario progreso = new ProgresoDiario();
        progreso.setFecha(LocalDate.now());
        progreso.setMinutosEstudio(120); // 2 hours
        when(progresoRepository.findByUsuarioIdAndFechaBetween(eq(userId), any(), any()))
                .thenReturn(List.of(progreso));

        // Mock Stats
        when(resumenRepository.countByUsuarioId(userId)).thenReturn(5L);
        when(evaluacionRepository.countByUsuarioId(userId)).thenReturn(3L);
        when(chatbotService.countChatsByUserId(userId)).thenReturn(10L);

        DashboardDTO result = dashboardService.getDashboardData(userId);

        assertNotNull(result);
        assertEquals(2.0, result.getProgresoSemanal().getHorasActuales());
        assertEquals(5L, result.getEstadisticas().getResumenes());
        assertEquals(3L, result.getEstadisticas().getEvaluaciones());
        assertEquals(10L, result.getEstadisticas().getChatsIA());
    }
}
