package com.learning.cliente_app.podcast.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servicio de extracción de texto de archivos (PDF, imágenes, DOCX, etc.)
 * 
 * NOTA: Este servicio usaba endpoints específicos de OpenAI (/v1/files y
 * /v1/responses)
 * que no están disponibles en DeepSeek.
 * 
 * Opciones alternativas:
 * - Usar bibliotecas locales como Apache PDFBox para PDFs, Apache POI para
 * Office, Tesseract para OCR
 * - Usar servicios de terceros como Google Cloud Vision API, AWS Textract
 * - Implementar una solución híbrida con procesamiento local
 */
@Service
public class ExtractService {

    /**
     * Extrae texto de un archivo (PDF, imagen, DOCX, etc.)
     * 
     * TEMPORALMENTE DESHABILITADO: DeepSeek no ofrece endpoints de procesamiento de
     * archivos.
     */
    public String extractText(MultipartFile file) throws Exception {
        throw new UnsupportedOperationException(
                "El servicio de extracción de texto de archivos no está disponible actualmente. " +
                        "DeepSeek no ofrece endpoints equivalentes a /v1/files de OpenAI. " +
                        "Por favor, considera usar bibliotecas locales (Apache PDFBox, Apache POI, Tesseract) " +
                        "o servicios de terceros (Google Cloud Vision, AWS Textract).");
    }
}
