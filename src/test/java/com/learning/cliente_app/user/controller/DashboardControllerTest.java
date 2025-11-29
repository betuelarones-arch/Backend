package com.learning.cliente_app.user.controller;

import com.learning.cliente_app.user.dto.DashboardDTO;
import com.learning.cliente_app.user.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private DashboardController dashboardController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(dashboardController).build();
    }

    @Test
    void getDashboardData_ShouldReturnOk() throws Exception {
        DashboardDTO.ProgresoSemanalDTO progreso = new DashboardDTO.ProgresoSemanalDTO(
                10.0, 30.0, "+10%", Collections.emptyList());
        DashboardDTO.EstadisticasDTO stats = new DashboardDTO.EstadisticasDTO(
                5, 3, 10, 5);
        DashboardDTO dashboardDTO = new DashboardDTO(progreso, stats, Collections.emptyList());

        when(dashboardService.getDashboardData(anyLong())).thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/v1/dashboard/resumen")
                .param("userId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.progresoSemanal.horasActuales").value(10.0))
                .andExpect(jsonPath("$.estadisticas.resumenes").value(5));
    }
}
