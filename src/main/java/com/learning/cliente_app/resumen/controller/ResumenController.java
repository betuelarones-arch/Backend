package com.learning.cliente_app.resumen.controller;

import com.learning.cliente_app.resumen.service.ResumenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/resumen")
public class ResumenController {

    private final ResumenService resumenService;

    public ResumenController(ResumenService resumenService) {
        this.resumenService = resumenService;
    }

    /**
     * POST /api/resumen/text
     * Genera un resumen a partir de texto plano.
     */
    @PostMapping("/text")
    public ResponseEntity<?> resumirTexto(@RequestBody Map<String, String> request) {
        try {
            String texto = request.get("texto");
            if (texto == null || texto.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El campo 'texto' es requerido"));
            }

            String resumen = resumenService.resumirTexto(texto);
            return ResponseEntity.ok(createSuccessResponse(resumen));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al generar resumen: " + e.getMessage()));
        }
    }

    /**
     * POST /api/resumen/file
     * Genera un resumen a partir de un archivo subido.
     */
    @PostMapping("/file")
    public ResponseEntity<?> resumirArchivo(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El archivo está vacío"));
            }

            String resumen = resumenService.resumirArchivo(file);
            return ResponseEntity.ok(createSuccessResponse(resumen));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al procesar archivo: " + e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String resumen) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("resumen", resumen);
        response.put("timestamp", java.time.LocalDateTime.now().toString());
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }
}
