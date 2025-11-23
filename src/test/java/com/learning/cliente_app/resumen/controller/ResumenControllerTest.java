package com.learning.cliente_app.resumen.controller;

import com.learning.cliente_app.ClienteAppApplication;
import com.learning.cliente_app.resumen.service.ResumenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ClienteAppApplication.class)
@AutoConfigureMockMvc
public class ResumenControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ResumenService resumenService;

        @Test
        void resumirTexto_Success() throws Exception {
                when(resumenService.resumirTexto(anyString())).thenReturn("Resumen exitoso");

                String jsonBody = "{\"texto\": \"Texto de prueba\"}";

                mockMvc.perform(post("/api/resumen/text")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("success"))
                                .andExpect(jsonPath("$.resumen").value("Resumen exitoso"));
        }

        @Test
        void resumirTexto_MissingField_ReturnsBadRequest() throws Exception {
                String jsonBody = "{}";

                mockMvc.perform(post("/api/resumen/text")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonBody))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("error"));
        }

        @Test
        void resumirArchivo_Success() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "test.txt",
                                "text/plain",
                                "Contenido".getBytes());

                when(resumenService.resumirArchivo(any())).thenReturn("Resumen de archivo");

                mockMvc.perform(multipart("/api/resumen/file")
                                .file(file))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("success"))
                                .andExpect(jsonPath("$.resumen").value("Resumen de archivo"));
        }

        @Test
        void resumirArchivo_EmptyFile_ReturnsBadRequest() throws Exception {
                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                "empty.txt",
                                "text/plain",
                                new byte[0]);

                mockMvc.perform(multipart("/api/resumen/file")
                                .file(file))
                                .andExpect(status().isBadRequest())
                                .andExpect(jsonPath("$.status").value("error"));
        }
}
