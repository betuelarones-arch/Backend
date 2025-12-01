package com.learning.cliente_app.podcast.service;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Servicio de extracción de texto de archivos (PDF, DOCX, XLSX, PPTX, TXT, etc.)
 * utilizando Apache Tika para el análisis de documentos.
 */
@Service
public class ExtractService {

    // Formatos de archivo soportados
    private static final Set<String> SUPPORTED_MIME_TYPES = new HashSet<>(Arrays.asList(
            // Documentos
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "application/vnd.ms-powerpoint",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "text/plain",
            "text/csv",
            "application/rtf",
            // OpenDocument formats
            "application/vnd.oasis.opendocument.text",
            "application/vnd.oasis.opendocument.spreadsheet",
            "application/vnd.oasis.opendocument.presentation"
    ));

    // Tamaño máximo de archivo: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    /**
     * Extrae texto de un archivo (PDF, DOCX, XLSX, PPTX, TXT, etc.)
     * 
     * @param file Archivo del cual extraer el texto
     * @return Texto extraído del archivo
     * @throws Exception Si el archivo no es válido o no se puede extraer el texto
     */
    public String extractText(MultipartFile file) throws Exception {
        // Validar que el archivo no esté vacío
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo está vacío");
        }

        // Validar el tamaño del archivo
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("El archivo excede el tamaño máximo permitido de 10MB");
        }

        // Obtener el tipo MIME del archivo
        String contentType = file.getContentType();
        if (contentType == null) {
            contentType = detectContentType(file.getOriginalFilename());
        }

        // Validar el tipo de archivo
        if (!isSupportedContentType(contentType)) {
            throw new IllegalArgumentException("Tipo de archivo no soportado: " + contentType + 
                    ". Formatos soportados: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT, CSV, RTF, ODT, ODS, ODP");
        }

        // Extraer el texto usando Apache Tika
        try (InputStream stream = new ByteArrayInputStream(file.getBytes())) {
            return extractTextFromStream(stream, contentType);
        } catch (IOException e) {
            throw new IOException("Error al leer el archivo: " + e.getMessage(), e);
        } catch (TikaException | SAXException e) {
            throw new Exception("Error al procesar el archivo: " + e.getMessage(), e);
        }
    }

    /**
     * Extrae texto de un flujo de entrada usando Apache Tika
     */
    private String extractTextFromStream(InputStream stream, String contentType) 
            throws IOException, TikaException, SAXException {
        // Configurar el manejador de contenido
        BodyContentHandler handler = new BodyContentHandler(-1); // -1 para sin límite de caracteres
        Metadata metadata = new Metadata();
        
        // Configurar el parser automático de Tika
        Parser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();
        
        // Procesar el documento
        try {
            parser.parse(stream, handler, metadata, context);
            return handler.toString().trim();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignorar error al cerrar el stream
            }
        }
    }

    /**
     * Detecta el tipo MIME basado en la extensión del archivo
     */
    private String detectContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String lowerFileName = fileName.toLowerCase();
        
        if (lowerFileName.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFileName.endsWith(".doc")) {
            return "application/msword";
        } else if (lowerFileName.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (lowerFileName.endsWith(".xls")) {
            return "application/vnd.ms-excel";
        } else if (lowerFileName.endsWith(".xlsx")) {
            return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else if (lowerFileName.endsWith(".ppt")) {
            return "application/vnd.ms-powerpoint";
        } else if (lowerFileName.endsWith(".pptx")) {
            return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        } else if (lowerFileName.endsWith(".txt") || lowerFileName.endsWith(".text")) {
            return "text/plain";
        } else if (lowerFileName.endsWith(".csv")) {
            return "text/csv";
        } else if (lowerFileName.endsWith(".rtf")) {
            return "application/rtf";
        } else if (lowerFileName.endsWith(".odt")) {
            return "application/vnd.oasis.opendocument.text";
        } else if (lowerFileName.endsWith(".ods")) {
            return "application/vnd.oasis.opendocument.spreadsheet";
        } else if (lowerFileName.endsWith(".odp")) {
            return "application/vnd.oasis.opendocument.presentation";
        }
        
        return "application/octet-stream";
    }

    /**
     * Verifica si el tipo MIME es soportado
     */
    private boolean isSupportedContentType(String contentType) {
        if (contentType == null) {
            return false;
        }
        
        // Verificar si el tipo MIME exacto está en la lista de soportados
        if (SUPPORTED_MIME_TYPES.contains(contentType.toLowerCase())) {
            return true;
        }
        
        // Verificar si algún patrón coincide (por ejemplo, application/vnd.ms-*)
        for (String mimeType : SUPPORTED_MIME_TYPES) {
            if (contentType.toLowerCase().startsWith(mimeType.split("/")[0])) {
                return true;
            }
        }
        
        return false;
    }
}
