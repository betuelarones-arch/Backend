package com.learning.cliente_app.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.Date;

@Configuration
@Profile("test")
public class TestFirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials
                                .create(new AccessToken("fake-token", Date.from(Instant.now().plusSeconds(3600)))))
                        .setProjectId("test-project")
                        .build();
                FirebaseApp.initializeApp(options);
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize mock FirebaseApp", e);
            }
        }
    }
}
