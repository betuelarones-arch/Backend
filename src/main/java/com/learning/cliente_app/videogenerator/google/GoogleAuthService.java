package com.learning.cliente_app.videogenerator.google;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.util.Collections;

@Service
public class GoogleAuthService {

    @Value("${gcp.credentials.path}")
    private String credentialsPath;

    @Value("${gcp.project.id}")
    private String projectId;

    public String obtenerAccessToken() {
        try {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singletonList("https://www.googleapis.com/auth/cloud-platform"));

            credentials.refreshIfExpired();

            return credentials.getAccessToken().getTokenValue();

        } catch (Exception e) {
            throw new RuntimeException("Error obteniendo token GCP: " + e.getMessage());
        }
    }

    public String getProjectId() {
        return projectId;
    }
}