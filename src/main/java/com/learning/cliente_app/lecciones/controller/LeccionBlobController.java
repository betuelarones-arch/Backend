package com.learning.cliente_app.lecciones.controller;

import com.learning.cliente_app.lecciones.model.LeccionEntity;
import com.learning.cliente_app.lecciones.repository.LeccionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoint para subir archivos PPT binarios a lecciones existentes.
 * Almacena el contenido del archivo directamente en la columna ppt_blob.
 */
@RestController
@RequestMapping("/api/lecciones")
public class LeccionBlobController {

    private final LeccionRepository leccionRepository;

    public LeccionBlobController(LeccionRepository leccionRepository) {
        this.leccionRepository = leccionRepository;
    }

    /**
     * POST /api/lecciones/{id}/ppt-upload
     * Sube un archivo PPT y lo guarda como blob en la lección.
     */
    @PostMapping("/{id}/ppt-upload")
    public ResponseEntity<?> uploadPptBlob(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            LeccionEntity leccion = leccionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lección no encontrada: " + id));

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            leccion.setPptBlob(file.getBytes());
            leccionRepository.save(leccion);

            return ResponseEntity.ok().body(new BlobUploadResponse(
                    "PPT blob guardado exitosamente",
                    id,
                    file.getSize(),
                    "application/vnd.ms-powerpoint"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al guardar PPT: " + e.getMessage()));
        }
    }

    /**
     * GET /api/lecciones/{id}/ppt-download
     * Descarga el blob PPT almacenado.
     */
    @GetMapping("/{id}/ppt-download")
    public ResponseEntity<?> downloadPptBlob(@PathVariable Long id) {
        try {
            LeccionEntity leccion = leccionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Lección no encontrada: " + id));

            if (leccion.getPptBlob() == null || leccion.getPptBlob().length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay PPT guardado para esta lección");
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=leccion_" + id + ".pptx")
                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.presentationml.presentation")
                    .body(leccion.getPptBlob());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al descargar PPT: " + e.getMessage()));
        }
    }

    // Response DTOs
    public static class BlobUploadResponse {
        public String message;
        public Long entityId;
        public Long fileSize;
        public String contentType;

        public BlobUploadResponse(String message, Long entityId, Long fileSize, String contentType) {
            this.message = message;
            this.entityId = entityId;
            this.fileSize = fileSize;
            this.contentType = contentType;
        }

        public String getMessage() { return message; }
        public Long getEntityId() { return entityId; }
        public Long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
    }

    public static class ErrorResponse {
        public String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() { return error; }
    }
}
