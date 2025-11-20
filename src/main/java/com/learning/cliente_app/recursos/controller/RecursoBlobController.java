package com.learning.cliente_app.recursos.controller;

import com.learning.cliente_app.recursos.model.RecursoEntity;
import com.learning.cliente_app.recursos.repository.RecursoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST endpoint para subir archivos de recursos (PDFs, documentos, etc).
 * Almacena el contenido del archivo directamente en la columna file_blob.
 */
@RestController
@RequestMapping("/api/recursos")
public class RecursoBlobController {

    private final RecursoRepository recursoRepository;

    public RecursoBlobController(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    /**
     * POST /api/recursos/{id}/file-upload
     * Sube un archivo de recurso y lo guarda como blob.
     */
    @PostMapping("/{id}/file-upload")
    public ResponseEntity<?> uploadFileBlob(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            RecursoEntity recurso = recursoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Recurso no encontrado: " + id));

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("El archivo está vacío");
            }

            recurso.setFileBlob(file.getBytes());
            recursoRepository.save(recurso);

            return ResponseEntity.ok().body(new BlobUploadResponse(
                    "Archivo recurso guardado exitosamente",
                    id,
                    file.getSize(),
                    file.getContentType()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al guardar archivo: " + e.getMessage()));
        }
    }

    /**
     * GET /api/recursos/{id}/file-download
     * Descarga el blob de archivo almacenado.
     */
    @GetMapping("/{id}/file-download")
    public ResponseEntity<?> downloadFileBlob(@PathVariable Long id) {
        try {
            RecursoEntity recurso = recursoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Recurso no encontrado: " + id));

            if (recurso.getFileBlob() == null || recurso.getFileBlob().length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No hay archivo guardado para este recurso");
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=recurso_" + id + ".bin")
                    .header("Content-Type", "application/octet-stream")
                    .body(recurso.getFileBlob());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error al descargar archivo: " + e.getMessage()));
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
