package com.learning.cliente_app.resumen.service;

import com.learning.cliente_app.podcast.service.ExtractService;
import com.learning.cliente_app.resumen.repository.ResumenRepository;
import com.learning.cliente_app.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
    private ResumenRepository resumenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile mockFile;

    @InjectMocks
    private ResumenService resumenService;

    @BeforeEach
    void setUp() {
        // resumenService is injected by @InjectMocks
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
    
    @SpringBootTest
    @ActiveProfiles("test")
    @ExtendWith(SpringExtension.class)
    public static class OpenAIConnectionTest {
        
        @Autowired(required = false)
        private ResumenService resumenService;
        
        @Test
        void testOpenAIConnection() {
            // Skip test if running in CI environment without API key
            if (System.getenv("CI") != null && System.getenv("CI").equalsIgnoreCase("true")) {
                return;
            }
            
            try {
                String testText = "Este es un texto de prueba para verificar la conexión con OpenAI.";
                String result = resumenService.resumirTexto(testText);
                assertNotNull(result);
                assertFalse(result.trim().isEmpty(), "La respuesta de la API no debe estar vacía");
                System.out.println("Conexión exitosa con OpenAI. Respuesta recibida: " + result);
            } catch (Exception e) {
                System.err.println("Error al conectar con OpenAI: " + e.getMessage());
                fail("No se pudo conectar con OpenAI. Verifica tu API key y conexión a internet. Error: " + e.getMessage());
            }
        }
    }
}
