package com.learning.cliente_app.podcast.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

// avoid test-only MockMultipartFile dependency; use a small internal MultipartFile implementation instead

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.learning.cliente_app.podcast.model.VideoRequest;
import com.learning.cliente_app.podcast.model.VideoResponse;

@Service
public class VideoService {

    @Value("${openai.api.key}")
    private String OPENAI_API_KEY;

    private final ExtractService extractService;
    private final ScriptService scriptService;
    private final AudioService audioService;

    public VideoService(ExtractService extractService, ScriptService scriptService, AudioService audioService) {
        this.extractService = extractService;
        this.scriptService = scriptService;
        this.audioService = audioService;
    }

    /**
     * Genera un video desde un documento PDF/DOCX/etc
     */
    public VideoResponse generateVideoFromDocument(MultipartFile document, String voice) {
        try {
            if (document.getOriginalFilename() != null && document.getOriginalFilename().endsWith(".docx")) {
                File convertedFile = convertDocxToTxt(document);
                document = new ByteArrayMultipartFile(
                    "document",
                    "converted.txt",
                    "text/plain",
                    Files.readAllBytes(convertedFile.toPath())
                );
            }

            String extractedText = extractService.extractText(document);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("No se pudo extraer texto del documento o está vacío");
            }

            String script = scriptService.generateScript(extractedText);
            if (script == null || script.trim().isEmpty()) {
                throw new RuntimeException("No se pudo generar el guion");
            }

            return generateVideoWithScript(document, script, voice);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar video desde documento: " + e.getMessage(), e);
        }
    }



    /**
     * Genera un video desde un prompt textual
     */
    public VideoResponse generateVideoFromPrompt(String prompt, MultipartFile imageFile, String voice) {
        try {
            String script = scriptService.generateScriptFromPrompt(prompt);
            if (script == null || script.trim().isEmpty()) {
                throw new RuntimeException("No se pudo generar el guion desde el prompt");
            }

            return generateVideoWithScript(imageFile, script, voice);

        } catch (Exception e) {
            throw new RuntimeException("Error al generar video desde prompt: " + e.getMessage(), e);
        }
    }

    /**
     * Genera el “video” combinando guion, voz y (opcionalmente) imagen
     */
    public VideoResponse generateVideoWithScript(MultipartFile file, String scriptText, String voice) {
        try {
            // 1️⃣ Generar audio con OpenAI
            File audioFile = audioService.generateAudio(scriptText);

            // 2️⃣ Si no se subió imagen, generamos una con DALL·E 3
            String imageUrl;
            if (file == null || file.isEmpty()) {
                imageUrl = generateImageWithDalle(scriptText); // 🔥 nuevo método
            } else {
                imageUrl = "data:image/jpeg;base64," + Base64.getEncoder()
                        .encodeToString(file.getBytes());
            }

            // 3️⃣ Construir respuesta
            VideoResponse videoResponse = new VideoResponse();
            videoResponse.setStatus("success");
            videoResponse.setScript(scriptText);
            videoResponse.setVoice(voice);
            videoResponse.setAudioUrl(audioFile.getAbsolutePath());
            videoResponse.setImageUrl(imageUrl);
            videoResponse.setMessage("Imagen y audio generados exitosamente (simulación de video).");

            return videoResponse;

        } catch (IOException e) {
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar video con OpenAI: " + e.getMessage(), e);
        }
    }


    /**
     * Compatibilidad con peticiones antiguas tipo VideoRequest
     */
    public VideoResponse generateVideo(MultipartFile file, VideoRequest request) {
        return generateVideoWithScript(file, request.getScriptText(), request.getVoice());
    }

    /**
     * Simula el estado del video (si después conectas algo real tipo Pika o RunwayML)
     */
    public VideoResponse checkVideoStatus(String videoId) {
        VideoResponse response = new VideoResponse();
        response.setStatus("completed");
        response.setMessage("El video se generó correctamente con OpenAI");
        return response;
    }

    public File generatePodcastFromDocument(MultipartFile document) {
        try {
            String extractedText = extractService.extractText(document);
            if (extractedText == null || extractedText.trim().isEmpty()) {
                throw new RuntimeException("No se pudo extraer texto del documento o está vacío");
            }
            String script = scriptService.generateScript(extractedText);

            File podcastFile = audioService.generateAudio(script);

            return podcastFile;

        } catch (Exception e) {
            throw new RuntimeException("Error al generar podcast: " + e.getMessage(), e);
        }
    }


    private String generateImageWithDalle(String prompt) throws IOException, InterruptedException {
        String apiUrl = "https://api.openai.com/v1/images/generations";

        String jsonBody = """
        {
            "model": "dall-e-3",
            "prompt": "%s",
            "size": "1024x1024"
        }
        """.formatted(prompt.replace("\"", "'"));

        ProcessBuilder processBuilder = new ProcessBuilder(
                "curl", apiUrl,
                "-H", "Content-Type: application/json",
                "-H", "Authorization: Bearer " + OPENAI_API_KEY,
                "-d", jsonBody
        );
    
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();
        byte[] responseBytes = process.getInputStream().readAllBytes();
        process.waitFor();

        String responseJson = new String(responseBytes);
        return extractImageUrl(responseJson);
    }

    private String extractImageUrl(String json) {
        int start = json.indexOf("\"url\":\"") + 7;
        int end = json.indexOf("\"", start);
        return start > 6 && end > start ? json.substring(start, end) : null;
    }

    public File convertDocxToTxt(MultipartFile file) throws Exception {
    File tempTxt = File.createTempFile("converted-", ".txt");
        try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
            FileWriter writer = new FileWriter(tempTxt)) {

            doc.getParagraphs().forEach(p -> {
                try {
                    writer.write(p.getText() + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return tempTxt;
    }
        
        
    // Minimal MultipartFile implementation to avoid test-only MockMultipartFile dependency
    private static class ByteArrayMultipartFile implements org.springframework.web.multipart.MultipartFile {
        private final String name;
        private final String originalFilename;
        private final String contentType;
        private final byte[] content;

        public ByteArrayMultipartFile(String name, String originalFilename, String contentType, byte[] content) {
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
            this.content = content == null ? new byte[0] : content;
        }

        @Override
        public String getName() { return name; }

        @Override
        public String getOriginalFilename() { return originalFilename; }

        @Override
        public String getContentType() { return contentType; }

        @Override
        public boolean isEmpty() { return content.length == 0; }

        @Override
        public long getSize() { return content.length; }

        @Override
        public byte[] getBytes() { return content; }

        @Override
        public java.io.InputStream getInputStream() { return new java.io.ByteArrayInputStream(content); }

        @Override
        public void transferTo(java.io.File dest) throws java.io.IOException, IllegalStateException {
            java.nio.file.Files.write(dest.toPath(), content);
        }
    }

}

