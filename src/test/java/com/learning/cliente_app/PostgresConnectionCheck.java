package com.learning.cliente_app;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test que intenta conectarse a la base de datos definida en src/main/resources/application.properties
 * Este test se ejecuta a demanda (lo lanzamos explícitamente) porque puede fallar si no hay DB accesible.
 */
public class PostgresConnectionCheck {

    @Test
    void tryConnectToPostgresFromMainProperties() throws Exception {
        Properties p = new Properties();
        // Prefer main resources file on disk so tests don't pick up src/test resources (H2)
        java.nio.file.Path mainProps = java.nio.file.Paths.get("src/main/resources/application.properties");
        if (java.nio.file.Files.exists(mainProps)) {
            try (InputStream is = java.nio.file.Files.newInputStream(mainProps)) {
                p.load(is);
            }
        } else {
            // fallback to classpath
            try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
                if (is == null) {
                    fail("No se encontró application.properties (ni en src/main/resources ni en classpath)");
                    return;
                }
                p.load(is);
            }
        }

        String url = p.getProperty("spring.datasource.url");
        String user = p.getProperty("spring.datasource.username");
        String pass = p.getProperty("spring.datasource.password");

        if (url == null || !url.startsWith("jdbc:postgresql")) {
            fail("La URL de datasource no parece ser PostgreSQL o está ausente: " + url + "\n(la prueba lee src/main/resources/application.properties)");
            return;
        }

        try {
            // Intentar cargar driver explícitamente
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                // Driver no disponible en classpath
                fail("Driver PostgreSQL no encontrado en el classpath: " + e.getMessage());
                return;
            }

            try (Connection c = DriverManager.getConnection(url, user, pass)) {
                if (c == null || c.isClosed()) {
                    fail("No se pudo obtener una conexión válida (nulo o cerrada)");
                }
                // OK si llegamos aquí
            }
        } catch (Exception ex) {
            fail("Error conectando a PostgreSQL: " + ex.getClass().getSimpleName() + " - " + ex.getMessage());
        }
    }
}
