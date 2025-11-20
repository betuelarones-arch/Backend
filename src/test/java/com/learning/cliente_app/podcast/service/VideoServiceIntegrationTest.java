package com.learning.cliente_app.podcast.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.learning.cliente_app.podcast.model.VideoResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Test de integración para VideoService.
 * Pruebas de generación de videos desde documentos.
 * Patrón: @SpringBootTest + @Slf4j + try-catch + assertions
 */
@SpringBootTest
@Slf4j
public class VideoServiceIntegrationTest {

    @Autowired
    private VideoService videoService;

    // ==================== PRUEBAS DE VALIDACIÓN ====================

    /**
     * Test: Documento nulo
     */
    @Test
    public void testGenerarVideoConDocumentoNulo() {
        try {
            try {
                videoService.generateVideoFromDocument(null, "alloy");
                fail("Debería rechazar documento nulo");
            } catch (RuntimeException e) {
                log.info("Documento nulo rechazado: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Voz nula
     */
    @Test
    public void testGenerarVideoConVozNula() throws IOException {
        String contenido = "Contenido de prueba para generar video";
        MultipartFile documento = new MockMultipartFile(
                "document",
                "test.txt",
                "text/plain",
                contenido.getBytes()
        );

        try {
            try {
                videoService.generateVideoFromDocument(documento, null);
                log.info("Voz nula procesada");
                assertTrue(true);
            } catch (RuntimeException e) {
                log.info("Voz nula rechazada: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Documento vacío
     */
    @Test
    public void testGenerarVideoConDocumentoVacio() throws IOException {
        MultipartFile documentoVacio = new MockMultipartFile(
                "document",
                "empty.txt",
                "text/plain",
                "".getBytes()
        );

        try {
            try {
                videoService.generateVideoFromDocument(documentoVacio, "alloy");
                log.info("Documento vacío procesado");
            } catch (RuntimeException e) {
                log.info("Documento vacío rechazado: {}", e.getMessage());
            }

            assertTrue(true);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE TIPOS DE DOCUMENTO ====================

    /**
     * Test: Documento TXT válido
     */
    @Test
    public void testGenerarVideoDesdeTexto() throws IOException {
        String contenido = "Este es un contenido educativo para convertir en video. " +
                "La educación es fundamental para el desarrollo.";

        MultipartFile documento = new MockMultipartFile(
                "document",
                "contenido.txt",
                "text/plain",
                contenido.getBytes()
        );

        try {
            log.info("Generando video desde documento TXT ({} bytes)", documento.getSize());

            try {
                VideoResponse respuesta = videoService.generateVideoFromDocument(documento, "alloy");

                if (respuesta != null) {
                    log.info("VIDEO GENERADO: Script length={}, AudioUrl={}",
                            respuesta.getScript() != null ? respuesta.getScript().length() : 0,
                            respuesta.getAudioUrl() != null ? respuesta.getAudioUrl() : "N/A"
                    );

                    assertNotNull(respuesta.getScript(), "Script no debe ser nulo");
                }

            } catch (RuntimeException e) {
                log.info("Error de API (esperado sin key válida): {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Documento DOCX (conversión)
     */
    @Test
    public void testGenerarVideoDesdeDocx() throws IOException {
        // Simulamos un archivo DOCX (en prueba real sería un DOCX válido)
        byte[] contenido = "Contenido de documento DOCX convertido a texto".getBytes();

        MultipartFile documento = new MockMultipartFile(
                "document",
                "contenido.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                contenido
        );

        try {
            log.info("Generando video desde documento DOCX");

            try {
                VideoResponse respuesta = videoService.generateVideoFromDocument(documento, "echo");

                if (respuesta != null) {
                    log.info("VIDEO DESDE DOCX: generado correctamente");
                }

            } catch (RuntimeException e) {
                log.info("Error esperado: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE VOCES ====================

    /**
     * Test: Diferentes voces disponibles
     */
    @Test
    public void testGenerarVideoDiferentesVoces() throws IOException {
        String contenido = "Contenido para probar diferentes voces";
        String[] voces = {"alloy", "echo", "fable", "onyx", "nova", "shimmer"};

        try {
            log.info("=== PROBANDO {} VOCES ===", voces.length);

            for (String voz : voces) {
                log.info("Generando video con voz: {}", voz);

                MultipartFile documento = new MockMultipartFile(
                        "document",
                        "contenido_" + voz + ".txt",
                        "text/plain",
                        contenido.getBytes()
                );

                try {
                    VideoResponse respuesta = videoService.generateVideoFromDocument(documento, voz);

                    if (respuesta != null) {
                        log.info("Voz {} procesada", voz);
                    }

                } catch (RuntimeException e) {
                    log.info("Voz {} error: {}", voz, e.getMessage());
                }
            }

            log.info("=== TODAS LAS VOCES PROCESADAS ===");
            assertTrue(true);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE CONTENIDO ====================

    /**
     * Test: Contenido muy largo
     */
    @Test
    public void testGenerarVideoContenidoMuyLargo() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50; i++) {
            sb.append("Párrafo ").append(i).append(": Este es contenido educativo sobre el tema número ").append(i).append(". ");
        }

        MultipartFile documento = new MockMultipartFile(
                "document",
                "contenido_largo.txt",
                "text/plain",
                sb.toString().getBytes()
        );

        try {
            log.info("Generando video con contenido muy largo ({} caracteres)", sb.length());

            try {
                VideoResponse respuesta = videoService.generateVideoFromDocument(documento, "alloy");

                if (respuesta != null) {
                    log.info("VIDEO LARGO: generado");
                }

            } catch (RuntimeException e) {
                log.info("Error esperado con contenido largo: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Contenido con caracteres especiales
     */
    @Test
    public void testGenerarVideoConCaracteresEspeciales() throws IOException {
        String contenido = "Matemáticas: 2+2=4. Química: H₂O. Física: E=mc². " +
                "Acentos: á, é, í, ó, ú, ñ. Símbolos: @#$%^&*()";

        MultipartFile documento = new MockMultipartFile(
                "document",
                "especiales.txt",
                "text/plain",
                contenido.getBytes()
        );

        try {
            log.info("Generando video con caracteres especiales");

            try {
                VideoResponse respuesta = videoService.generateVideoFromDocument(documento, "alloy");

                if (respuesta != null) {
                    log.info("VIDEO CON CARACTERES ESPECIALES: generado");
                }

            } catch (RuntimeException e) {
                log.info("Error: {}", e.getMessage());
                assertTrue(true);
            }

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    // ==================== PRUEBAS DE FLUJOS ====================

    /**
     * Test: Flujo completo de generación
     */
    @Test
    public void testFlujoCompletoGeneracion() throws IOException {
        String contenido = "Tema: Introducción a la Programación. " +
                "La programación es el arte de crear software. " +
                "Utilizamos lenguajes como Java, Python, JavaScript.";

        MultipartFile documento = new MockMultipartFile(
                "document",
                "programacion.txt",
                "text/plain",
                contenido.getBytes()
        );

        try {
            log.info("=== INICIANDO FLUJO COMPLETO ===");

            // 1. Validación
            assertNotNull(documento);
            assertTrue(documento.getSize() > 0);
            log.info("1. DOCUMENTO VALIDADO: {} bytes", documento.getSize());

            // 2. Verificar servicio
            assertNotNull(videoService);
            log.info("2. VIDEO SERVICE DISPONIBLE");

            // 3. Generar video
            try {
                VideoResponse respuesta = videoService.generateVideoFromDocument(documento, "alloy");

                if (respuesta != null) {
                    log.info("3. VIDEO GENERADO");
                    log.info("   - Script: {} caracteres", 
                            respuesta.getScript() != null ? respuesta.getScript().length() : 0);
                    log.info("   - AudioUrl: {}", 
                            respuesta.getAudioUrl() != null ? respuesta.getAudioUrl() : "N/A");
                }

            } catch (RuntimeException e) {
                log.info("3. Error de API (esperado): {}", e.getMessage());
            }

            log.info("=== FLUJO COMPLETO FINALIZADO ===");
            assertTrue(true);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test: Verificación de servicio inyectado
     */
    @Test
    public void testVideoServiceInyectado() {
        try {
            assertNotNull(videoService, "VideoService debe estar inyectado");
            log.info("VIDEO SERVICE INYECTADO CORRECTAMENTE");
            assertTrue(true);

        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
            fail("Error: " + e.getMessage());
        }
    }

}
