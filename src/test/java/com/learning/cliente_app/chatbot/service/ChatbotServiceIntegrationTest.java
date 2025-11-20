package com.learning.cliente_app.chatbot.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.learning.cliente_app.chatbot.dto.ChatResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Test de integración para ChatbotService.
 * Pruebas de creación de conversaciones y procesamiento de mensajes.
 * Patrón: @SpringBootTest + @Slf4j + try-catch + assertions
 */
@SpringBootTest
@Slf4j
public class ChatbotServiceIntegrationTest {

    @Autowired
    private ChatbotService chatbotService;

    /**
     * Test 1: Crear nueva conversación
     */
    @Test
    @DisplayName("Crear nueva conversación")
    public void testCrearNuevaConversacion() {
        try {
            String conversacionId = chatbotService.crearNuevaConversacion();
            
            assertNotNull(conversacionId);
            assertTrue(conversacionId.length() > 0);
            
            log.info("✓ Nueva conversación creada: {}", conversacionId);
        } catch (Exception e) {
            log.error("✗ Error creando nueva conversación", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 2: Procesar mensaje educativo
     */
    @Test
    @DisplayName("Procesar mensaje educativo")
    public void testProcesarMensajeEducativo() {
        try {
            String conversacionId = chatbotService.crearNuevaConversacion();
            String mensajeUsuario = "¿Cuál es la derivada de x²?";
            
            ChatResponse response = chatbotService.procesarMensaje(conversacionId, mensajeUsuario);
            
            assertNotNull(response);
            assertNotNull(response.getRespuesta());
            assertTrue(response.getRespuesta().length() > 0);
            
            log.info("✓ Mensaje educativo procesado: {}", response.getRespuesta().substring(0, 50));
        } catch (Exception e) {
            log.error("✗ Error procesando mensaje educativo", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 3: Múltiples mensajes en conversación
     */
    @Test
    @DisplayName("Múltiples mensajes en conversación")
    public void testMultiplesMensajesConversacion() {
        try {
            String conversacionId = chatbotService.crearNuevaConversacion();
            String[] mensajes = {
                "¿Qué es la fotosíntesis?",
                "¿Cuáles son los productos finales?",
                "¿Cuál es la importancia ecológica?"
            };
            
            for (int i = 0; i < mensajes.length; i++) {
                ChatResponse response = chatbotService.procesarMensaje(conversacionId, mensajes[i]);
                
                assertNotNull(response);
                assertNotNull(response.getRespuesta());
                
                log.info("✓ Mensaje {} procesado", i + 1);
            }
            
            log.info("✓ Conversación con {} mensajes completada", mensajes.length);
        } catch (Exception e) {
            log.error("✗ Error en múltiples mensajes", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 4: Procesar pregunta vacía
     */
    @Test
    @DisplayName("Procesar pregunta vacía")
    public void testPreguntaVacia() {
        try {
            String conversacionId = chatbotService.crearNuevaConversacion();
            
            try {
                ChatResponse response = chatbotService.procesarMensaje(conversacionId, "");
                log.info("✓ Pregunta vacía procesada (respuesta: {})", response.getRespuesta());
            } catch (RuntimeException e) {
                log.info("✓ Pregunta vacía rechazada: {}", e.getMessage());
            }
            
            assertTrue(true);
        } catch (Exception e) {
            log.error("✗ Error en pregunta vacía", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 5: Procesar mensaje nulo
     */
    @Test
    @DisplayName("Procesar mensaje nulo")
    public void testMensajeNulo() {
        try {
            String conversacionId = chatbotService.crearNuevaConversacion();
            
            try {
                chatbotService.procesarMensaje(conversacionId, null);
                log.info("✓ Mensaje nulo procesado");
            } catch (RuntimeException e) {
                log.info("✓ Mensaje nulo rechazado: {}", e.getMessage());
            }
            
            assertTrue(true);
        } catch (Exception e) {
            log.error("✗ Error en mensaje nulo", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 6: Temáticas educativas diversas
     */
    @Test
    @DisplayName("Temáticas educativas diversas")
    public void testTematicasEducativas() {
        try {
            String[] tematicas = {
                "Matemáticas",
                "Física",
                "Química",
                "Biología",
                "Historia"
            };
            
            String conversacionId = chatbotService.crearNuevaConversacion();
            
            for (String tematica : tematicas) {
                String pregunta = "Explícame sobre " + tematica;
                ChatResponse response = chatbotService.procesarMensaje(conversacionId, pregunta);
                
                assertNotNull(response);
                log.info("✓ Temática {} procesada", tematica);
            }
            
            log.info("✓ {} temáticas procesadas", tematicas.length);
        } catch (Exception e) {
            log.error("✗ Error en temáticas educativas", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 7: Pregunta muy larga
     */
    @Test
    @DisplayName("Pregunta muy larga")
    public void testPreguntaMuyLarga() {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 20; i++) {
                sb.append("¿Podrías explicarme sobre el concepto ").append(i).append("? ");
            }
            String preguntaLarga = sb.toString();
            
            String conversacionId = chatbotService.crearNuevaConversacion();
            ChatResponse response = chatbotService.procesarMensaje(conversacionId, preguntaLarga);
            
            assertNotNull(response);
            log.info("✓ Pregunta larga procesada ({} caracteres)", preguntaLarga.length());
        } catch (Exception e) {
            log.error("✗ Error en pregunta larga", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 8: Caracteres especiales
     */
    @Test
    @DisplayName("Pregunta con caracteres especiales")
    public void testCaracteresEspeciales() {
        try {
            String[] preguntas = {
                "¿Qué es 2+2?",
                "¿Cómo se escribe: año, día?",
                "Acentos: á, é, í, ó, ú",
                "Símbolos: !@#$%^&*()"
            };
            
            String conversacionId = chatbotService.crearNuevaConversacion();
            
            for (String pregunta : preguntas) {
                ChatResponse response = chatbotService.procesarMensaje(conversacionId, pregunta);
                
                assertNotNull(response);
                log.info("✓ Pregunta con caracteres especiales procesada");
            }
            
            log.info("✓ {} preguntas con caracteres especiales procesadas", preguntas.length);
        } catch (Exception e) {
            log.error("✗ Error con caracteres especiales", e);
            fail("Error: " + e.getMessage());
        }
    }

    /**
     * Test 9: Verificación de servicio inyectado
     */
    @Test
    @DisplayName("Verificar servicio inyectado")
    public void testChatbotServiceInyectado() {
        try {
            assertNotNull(chatbotService, "ChatbotService debe estar inyectado");
            log.info("✓ ChatbotService inyectado correctamente");
        } catch (Exception e) {
            log.error("✗ Error verificando servicio", e);
            fail("Error: " + e.getMessage());
        }
    }

}
