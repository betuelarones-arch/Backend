package com.learning.cliente_app.classroom.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRCodeService {

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    private static final String QR_CODE_FORMAT = "PNG";

    /**
     * Genera un código QR como array de bytes
     */
    public byte[] generarQRCodeBytes(String contenido) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(contenido, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, QR_CODE_FORMAT, pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    /**
     * Genera un código QR para unirse a una clase
     */
    public byte[] generarQRCodeParaClase(String codigoUnico) throws WriterException, IOException {
        String url = baseUrl + "/api/classroom/unirse/" + codigoUnico;
        return generarQRCodeBytes(url);
    }

    /**
     * Genera la URL para unirse a una clase
     */
    public String generarURLUnirse(String codigoUnico) {
        return baseUrl + "/api/classroom/unirse/" + codigoUnico;
    }
}

