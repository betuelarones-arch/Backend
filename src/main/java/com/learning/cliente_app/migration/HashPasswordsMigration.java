package com.learning.cliente_app.migration;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility standalone to migrate plain-text passwords in `core.usuarios` to bcrypt.
 * Usage: run locally with the same classpath as the application (driver present).
 * Make a backup before running.
 */
public class HashPasswordsMigration {

    public static void main(String[] args) throws Exception {
        String url = System.getProperty("jdbc.url", "jdbc:postgresql://localhost:5432/mi_basedatos");
        String user = System.getProperty("jdbc.user", "postgres");
        String pass = System.getProperty("jdbc.pass", "postgres");

        System.out.println("Connecting to: " + url);
        Class.forName("org.postgresql.Driver");

        try (Connection conn = DriverManager.getConnection(url, user, pass)) {
            conn.setAutoCommit(false);
            List<Long> updated = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement("SELECT id_usuario, contrasena FROM core.usuarios FOR UPDATE")) {
                try (ResultSet rs = ps.executeQuery()) {
                    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                    while (rs.next()) {
                        long id = rs.getLong("id_usuario");
                        String pw = rs.getString("contrasena");
                        if (pw == null) continue;
                        if (pw.startsWith("$2a$") || pw.startsWith("$2b$") || pw.startsWith("$2y$")) {
                            // already bcrypt
                            continue;
                        }
                        String hashed = encoder.encode(pw);
                        try (PreparedStatement up = conn.prepareStatement("UPDATE core.usuarios SET contrasena = ? WHERE id_usuario = ?")) {
                            up.setString(1, hashed);
                            up.setLong(2, id);
                            up.executeUpdate();
                            updated.add(id);
                        }
                    }
                }
            }
            conn.commit();
            System.out.println("Updated users: " + updated.size());
            if (!updated.isEmpty()) System.out.println(updated);
        }
    }
}
