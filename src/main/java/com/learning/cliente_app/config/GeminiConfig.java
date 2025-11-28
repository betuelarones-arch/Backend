package com.learning.cliente_app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GeminiConfig {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Value("${gemini.api.host:https://generativelanguage.googleapis.com}")
    private String geminiApiHost;

    @Bean
    public WebClient geminiWebClient() {
        return WebClient.builder()
                .baseUrl(geminiApiHost)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getGeminiApiKey() {
        return geminiApiKey;
    }

}
