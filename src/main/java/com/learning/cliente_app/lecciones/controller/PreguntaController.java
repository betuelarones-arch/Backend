package com.learning.cliente_app.lecciones.controller;


import com.learning.cliente_app.lecciones.dto.PreguntaUpdateDTO;
import com.learning.cliente_app.lecciones.model.Pregunta;
import com.learning.cliente_app.lecciones.service.GeneradorPreguntasService;
import com.learning.cliente_app.lecciones.service.PreguntaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/preguntas")
@CrossOrigin(origins = "*")
public class PreguntaController {

    private final PreguntaService preguntaService;
    private final GeneradorPreguntasService generadorPreguntasService;

    public PreguntaController(PreguntaService preguntaService,
                              GeneradorPreguntasService generadorPreguntasService) {
        this.preguntaService = preguntaService;
        this.generadorPreguntasService = generadorPreguntasService;
    }

    /**
     * POST /api/preguntas/subir
     * Sube un archivo, extrae texto y genera preguntas
     */
    @PostMapping("/subir")
    public ResponseEntity<?> subirArchivo(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "numPreguntas", required = false) Integer numPreguntas) {

        try {
            // Si no se especifica, usar valor por defecto
            if (numPreguntas == null) {
                numPreguntas = generadorPreguntasService.getDefaultPreguntas();
            }

            // Validar límites antes de procesar
            int min = generadorPreguntasService.getMinPreguntas();
            int max = generadorPreguntasService.getMaxPreguntas();

            if (numPreguntas < min || numPreguntas > max) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", String.format("El número de preguntas debe estar entre %d y %d", min, max));
                error.put("minimo", min);
                error.put("maximo", max);
                error.put("valor_recibido", numPreguntas);
                return ResponseEntity.badRequest().body(error);
            }

            List<Pregunta> preguntas = preguntaService.procesarArchivoYGenerarPreguntas(file, numPreguntas);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Preguntas generadas exitosamente");
            response.put("cantidad", preguntas.size());
            response.put("solicitadas", numPreguntas);
            response.put("preguntas", preguntas);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    /**
     * GET /api/preguntas/limites
     * Obtiene los límites configurados para generación de preguntas
     */
    @GetMapping("/limites")
    public ResponseEntity<Map<String, Integer>> obtenerLimites() {
        Map<String, Integer> limites = new HashMap<>();
        limites.put("minimo", generadorPreguntasService.getMinPreguntas());
        limites.put("maximo", generadorPreguntasService.getMaxPreguntas());
        limites.put("por_defecto", generadorPreguntasService.getDefaultPreguntas());
        return ResponseEntity.ok(limites);
    }

    /**
     * GET /api/preguntas
     * Obtiene todas las preguntas
     */
    @GetMapping
    public ResponseEntity<List<Pregunta>> obtenerTodasLasPreguntas() {
        List<Pregunta> preguntas = preguntaService.obtenerTodasLasPreguntas();
        return ResponseEntity.ok(preguntas);
    }

    /**
     * GET /api/preguntas/archivo/{nombreArchivo}
     * Obtiene preguntas por archivo
     */
    @GetMapping("/archivo/{nombreArchivo}")
    public ResponseEntity<List<Pregunta>> obtenerPreguntasPorArchivo(@PathVariable String nombreArchivo) {
        List<Pregunta> preguntas = preguntaService.obtenerPreguntasPorArchivo(nombreArchivo);
        return ResponseEntity.ok(preguntas);
    }

    /**
     * DELETE /api/preguntas/archivo/{nombreArchivo}
     * Elimina preguntas por archivo
     */
    @DeleteMapping("/archivo/{nombreArchivo}")
    public ResponseEntity<?> eliminarPreguntasPorArchivo(@PathVariable String nombreArchivo) {
        try {
            preguntaService.eliminarPreguntasPorArchivo(nombreArchivo);
            return ResponseEntity.ok(Map.of("mensaje", "Preguntas eliminadas exitosamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarPregunta(
            @PathVariable Long id,
            @RequestBody PreguntaUpdateDTO preguntaUpdate) {
        try {
            Pregunta preguntaActualizada = preguntaService.actualizarPregunta(id, preguntaUpdate);

            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Pregunta actualizada exitosamente");
            response.put("pregunta", preguntaActualizada);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPregunta(@PathVariable Long id) {
        try {
            preguntaService.eliminarPreguntaPorId(id);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Pregunta eliminada exitosamente",
                    "id", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}