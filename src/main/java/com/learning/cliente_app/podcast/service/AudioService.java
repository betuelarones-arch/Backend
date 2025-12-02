package com.learning.cliente_app.podcast.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AudioService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    @Value("${openai.api.host:https://api.openai.com}")
    private String OPENAI_API_HOST;

    public File generateAudio(String text) throws Exception {
        if (text == null || text.trim().isEmpty()) {
            throw new RuntimeException("El texto para audio está vacío");
        }

        File output = new File("output.mp3");

        String cleanText = text.replace("\r", "").replace("\n", " ").replace("\"", "\\\"");

        String jsonBody = "{"
                + "\"model\": \"gpt-4o-mini-tts\","
                + "\"voice\": \"alloy\","
                + "\"input\": \"" + cleanText + "\""
                + "}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_HOST + "/v1/audio/speech"))
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            // Leer el cuerpo de error para debug
            String errorBody = new String(response.body().readAllBytes());
            throw new RuntimeException("Error al generar audio con OpenAI: HTTP "
                    + response.statusCode() + " - " + errorBody);
        }

        try (InputStream in = response.body(); OutputStream out = new FileOutputStream(output)) {
            in.transferTo(out);
        }

        return output;
    }

}