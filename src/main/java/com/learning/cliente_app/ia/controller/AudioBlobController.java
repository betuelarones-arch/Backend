package com.learning.cliente_app.ia.controller;

import com.learning.cliente_app.ia.dto.BlobUploadResponse;
import com.learning.cliente_app.ia.dto.ErrorResponse;
import com.learning.cliente_app.ia.model.AudioIaEntity;
import com.learning.cliente_app.ia.repository.AudioIaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoint para subir archivos de audio generados por IA.
 * Almacena el contenido del audio directamente en la columna audio_blob.
 */
@RestController
@RequestMapping("/api/audios-ia")
public class AudioBlobController {

    private final AudioIaRepository audioIaRepository;

    public AudioBlobController(AudioIaRepository audioIaRepository) {
        this.audioIaRepository = audioIaRepository;
    }

    /**
     * POST /api/audios-ia/{id}/audio-upload
     * Sube un archivo de audio y lo guarda como blob.
     */
    @PostMapping("/{id}/audio-upload")
    public ResponseEntity<?> uploadAudioBlob(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            AudioIaEntity audioIa = audioIaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Audio IA no encontrado: " + id));

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo de audio está vacío");
            }

            audioIa.setAudioBlob(file.getBytes());
            audioIaRepository.save(audioIa);

            return ResponseEntity.ok().body(new BlobUploadResponse(
                    "Audio blob guardado exitosamente",
                    id,
                    file.getSize(),
                    "audio/mpeg"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al guardar audio: " + e.getMessage()));
        }
    }

    /**
     * GET /api/audios-ia/{id}/audio-download
     * Descarga el blob de audio almacenado.
     */
    @GetMapping("/{id}/audio-download")
    public ResponseEntity<?> downloadAudioBlob(@PathVariable Long id) {
        try {
            AudioIaEntity audioIa = audioIaRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Audio IA no encontrado: " + id));

            if (audioIa.getAudioBlob() == null || audioIa.getAudioBlob().length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay audio guardado para este registro");
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=audio_" + id + ".mp3")
                    .header("Content-Type", "audio/mpeg")
                    .body(audioIa.getAudioBlob());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al descargar audio: " + e.getMessage()));
        }
    }
}
