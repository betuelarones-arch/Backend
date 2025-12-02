package com.learning.cliente_app.podcast.controller;

import com.learning.cliente_app.podcast.model.VideoRequest;
import com.learning.cliente_app.podcast.model.VideoResponse;
import com.learning.cliente_app.podcast.service.VideoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/video")
public class AudioController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 🔹 Genera un video desde un documento (PDF, DOC, DOCX, TXT)
     * El sistema extrae el texto, genera un guion con IA y luego crea el video
     */
    @PostMapping(
            value = "/generate-from-document",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> generateVideoFromDocument(
            @RequestPart("document") MultipartFile document,
            @RequestPart(value = "voice", required = false) String voice
    ) {
        try {
            if (document == null || document.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El documento es requerido"));
            }

            VideoResponse response = videoService.generateVideoFromDocument(document, voice);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al generar video desde documento: " + e.getMessage()));
        }
    }

    /**
     * 🔹 Genera un video desde un prompt de texto
     * El sistema genera un guion con IA desde el prompt y luego crea el video
     */
    @PostMapping(
            value = "/generate-from-prompt",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> generateVideoFromPrompt(
            @RequestPart("prompt") String prompt,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestPart(value = "voice", required = false) String voice
    ) {
        try {
            if (prompt == null || prompt.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El prompt es requerido"));
            }

            VideoResponse response = videoService.generateVideoFromPrompt(prompt, imageFile, voice);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al generar video desde prompt: " + e.getMessage()));
        }
    }

    /**
     * 🔹 Endpoint original para generar video con guion ya proporcionado
     * Mantiene compatibilidad con el endpoint anterior
     */
    @PostMapping( value = "/generate", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> generateVideo(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestPart("script") String scriptText,
            @RequestPart(value = "voice", required = false) String voice
    ) {
        try {
            if (scriptText == null || scriptText.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El guion es requerido"));
            }

            VideoRequest request = new VideoRequest(scriptText, voice);
            VideoResponse response = videoService.generateVideo(file, request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al generar video: " + e.getMessage()));
        }
    }

    /**
     * 🔹 Endpoint para obtener estado del video
     */
    @GetMapping("/status/{id}")
    public ResponseEntity<?> checkStatus(@PathVariable String id) {
        try {
            if (id == null || id.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El ID del video es requerido"));
            }

            VideoResponse response = videoService.checkVideoStatus(id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener el estado del video: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", java.time.LocalDateTime.now().toString());
        return error;
    }

    @PostMapping(
            value = "/generate-podcast",
            consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }
    )
    public ResponseEntity<?> generatePodcast(
            @RequestPart("document") MultipartFile document) {
        try {
            File podcastFile = videoService.generatePodcastFromDocument(document);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Content-Disposition", "attachment; filename=podcast.mp3")
                    .body(new FileSystemResource(podcastFile));

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al generar el podcast: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }


}