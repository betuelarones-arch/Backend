package com.learning.cliente_app.user.controller;

import com.learning.cliente_app.user.dto.DashboardDTO;
import com.learning.cliente_app.user.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/resumen")
    public ResponseEntity<DashboardDTO> getDashboardData(@RequestParam Long userId) {
        DashboardDTO dashboardData = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }
}
