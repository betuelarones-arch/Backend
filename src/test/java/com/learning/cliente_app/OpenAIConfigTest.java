package com.learning.cliente_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=update",
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
public class OpenAIConfigTest {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.host}")
    private String apiHost;

    @Test
    public void testApiKeyIsSet() {
        try {
            assertNotNull(apiKey, "OpenAI API key should not be null");
            assertFalse(apiKey.trim().isEmpty(), "OpenAI API key should not be empty");
            assertFalse(apiKey.contains("PEGAR_TU_API_KEY_AQUI"), "Please replace the placeholder with your actual API key");
            System.out.println("✅ API Key is set correctly");
            
            assertNotNull(apiHost, "API Host should not be null");
            assertFalse(apiHost.trim().isEmpty(), "API Host should not be empty");
            System.out.println("🌐 API Host: " + apiHost);
            
            // If we get here, the test passed
            System.out.println("✅ Test completed successfully!");
        } catch (AssertionError e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            throw e;
        }
    }
}
