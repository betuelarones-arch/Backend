package com.learning.cliente_app.resumen.service;

import com.learning.cliente_app.podcast.service.ExtractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResumenServiceTest {

    @Mock
    private ExtractService extractService;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private MultipartFile mockFile;

    private ResumenService resumenService;

    @BeforeEach
    void setUp() {
        resumenService = new ResumenService(extractService, restTemplate);
    }

    @Test
    void resumirTexto_Success() {
        String texto = "Texto de prueba";
        // Formato de respuesta de Gemini API
        String mockResponse = "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"Resumen generado\"}]}}]}";

        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        String resultado = resumenService.resumirTexto(texto);

        assertEquals("Resumen generado", resultado);
    }

    @Test
    void resumirTexto_EmptyInput_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> resumenService.resumirTexto(""));
    }

    @Test
    void resumirArchivo_Success() throws Exception {
        String extractedText = "Contenido del archivo";
        // Formato de respuesta de Gemini API
        String mockResponse = "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"Resumen de archivo\"}]}}]}";

        when(extractService.extractText(mockFile)).thenReturn(extractedText);
        when(restTemplate.exchange(
                anyString(),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        String resultado = resumenService.resumirArchivo(mockFile);

        assertEquals("Resumen de archivo", resultado);
    }

    @Test
    void resumirArchivo_ExtractionFails_ThrowsException() throws Exception {
        when(extractService.extractText(mockFile)).thenThrow(new RuntimeException("Error de extracción"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> resumenService.resumirArchivo(mockFile));
        assertTrue(exception.getMessage().contains("Error al procesar el archivo"));
    }
}
