package com.learning.cliente_app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ClienteAppApplication.class)
public class DatabaseConnectivityTest {

    @Autowired
    private DataSource dataSource;

    @Test
    void dataSource_shouldProvideConnection() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            assertThat(c).isNotNull();
            assertThat(c.isValid(2)).isTrue();
        }
    }
}
