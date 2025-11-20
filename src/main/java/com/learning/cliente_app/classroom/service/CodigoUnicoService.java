package com.learning.cliente_app.classroom.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Random;

@Service
public class CodigoUnicoService {

    private static final String ALPHANUMERIC = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // Sin 0, O, I, 1 para evitar confusión
    private static final int CODE_LENGTH = 8;
    private final Random random = new Random();

    /**
     * Genera un código único alfanumérico de 8 caracteres
     */
    public String generarCodigoUnico() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return code.toString();
    }

    /**
     * Genera un UUID único para códigos más seguros
     */
    public String generarUUID() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    /**
     * Genera un código corto para clases (6 caracteres)
     */
    public String generarCodigoCorto() {
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length())));
        }
        return code.toString();
    }
}

