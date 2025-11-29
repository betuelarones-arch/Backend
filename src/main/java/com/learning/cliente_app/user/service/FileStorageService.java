package com.learning.cliente_app.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestionar el almacenamiento de archivos (fotos de perfil).
 */
@Service
public class FileStorageService {

    @Value("${app.upload.dir:uploads/profile-photos}")
    private String uploadDir;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    /**
     * Guarda un archivo en el sistema de archivos.
     * 
     * @return La ruta relativa del archivo guardado
     */
    public String guardarArchivo(MultipartFile file, Long userId) throws IOException {
        // Validar archivo
        validarImagen(file);

        // Crear directorio si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre de archivo único
        String extension = obtenerExtension(file.getOriginalFilename());
        String fileName = "user_" + userId + "_" + UUID.randomUUID().toString() + "." + extension;

        Path filePath = uploadPath.resolve(fileName);

        // Guardar archivo
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }

    /**
     * Elimina un archivo del sistema de archivos.
     */
    public void eliminarArchivo(String fileName) throws IOException {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir).resolve(fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }

    /**
     * Valida que el archivo sea una imagen válida.
     */
    public void validarImagen(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido (5MB)");
        }

        String extension = obtenerExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Formato de archivo no permitido. Use: " +
                    String.join(", ", ALLOWED_EXTENSIONS));
        }

        // Validar el content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
    }

    /**
     * Obtiene la extensión de un archivo.
     */
    private String obtenerExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Nombre de archivo inválido");
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
