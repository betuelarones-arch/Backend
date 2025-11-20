package com.learning.cliente_app.podcast.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ExtractService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Extrae texto de un archivo (PDF, imagen, DOCX, etc.) usando GPT-4o.
     */
    public String extractText(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new RuntimeException("Archivo sin nombre");

        // 1️⃣ Subir archivo al endpoint de OpenAI /v1/files
        String uploadUrl = "https://api.openai.com/v1/files";

        HttpHeaders uploadHeaders = new HttpHeaders();
        uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        uploadHeaders.setBearerAuth(OPENAI_API_KEY);

        ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return fileName;
            }
        };

        MultiValueMap<String, Object> uploadBody = new LinkedMultiValueMap<>();
        uploadBody.add("purpose", "assistants");
        uploadBody.add("file", fileResource);

        HttpEntity<MultiValueMap<String, Object>> uploadEntity = new HttpEntity<>(uploadBody, uploadHeaders);

        ResponseEntity<String> uploadResponse = restTemplate.exchange(
                uploadUrl, HttpMethod.POST, uploadEntity, String.class
        );

        if (!uploadResponse.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al subir archivo a OpenAI: " + uploadResponse);
        }

        JsonNode uploadJson = objectMapper.readTree(uploadResponse.getBody());
        String fileId = uploadJson.get("id").asText();

        // 2️⃣ Enviar solicitud de análisis al endpoint /v1/responses
        String responseUrl = "https://api.openai.com/v1/responses";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        String prompt = "Extrae todo el texto legible de este archivo y devuélvelo en español sin formato adicional.";

        String jsonBody = "{"
            + "\"model\": \"gpt-4o-mini\","
            + "\"input\": [{"
            + "   \"role\": \"user\","
            + "   \"content\": ["
            + "       {\"type\": \"input_text\", \"text\": \"" + prompt.replace("\"", "\\\"") + "\"},"
            + "       {\"type\": \"input_file\", \"file_id\": \"" + fileId + "\"}"
            + "   ]"
            + "}]"
            + "}";


        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                responseUrl, HttpMethod.POST, entity, String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Error al extraer texto con OpenAI: " + response.getBody());
        }

        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        JsonNode outputNode = jsonNode.at("/output/0/content/0/text");

        return outputNode.isMissingNode() ? "[No se encontró texto]" : outputNode.asText().trim();
    }
}
