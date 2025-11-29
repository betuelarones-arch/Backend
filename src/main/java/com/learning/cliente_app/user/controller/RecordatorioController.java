package com.learning.cliente_app.user.controller;

import com.learning.cliente_app.user.dto.RecordatorioDTO;
import com.learning.cliente_app.user.service.RecordatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recordatorios")
public class RecordatorioController {

    private final RecordatorioService recordatorioService;

    public RecordatorioController(RecordatorioService recordatorioService) {
        this.recordatorioService = recordatorioService;
    }

    @GetMapping
    public ResponseEntity<List<RecordatorioDTO>> listar(@RequestParam Long userId) {
        return ResponseEntity.ok(recordatorioService.listarPorUsuario(userId));
    }

    @PostMapping
    public ResponseEntity<RecordatorioDTO> crear(@RequestParam Long userId, @RequestBody RecordatorioDTO dto) {
        return ResponseEntity.ok(recordatorioService.crear(userId, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        recordatorioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<RecordatorioDTO> toggleCompletado(@PathVariable Long id) {
        return ResponseEntity.ok(recordatorioService.toggleCompletado(id));
    }
}
