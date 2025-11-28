package com.learning.cliente_app.podcast.service;

import java.io.File;

import org.springframework.stereotype.Service;

/**
 * Servicio de generación de audio (Text-to-Speech)
 * 
 * NOTA: DeepSeek no ofrece un servicio nativo de Text-to-Speech.
 * Este servicio está temporalmente deshabilitado.
 * 
 * Opciones alternativas:
 * - Usar un servicio de terceros como ElevenLabs, Google TTS, o Amazon Polly
 * - Mantener OpenAI solo para esta funcionalidad
 * - Implementar una solución de TTS local
 */
@Service
public class AudioService {

    public File generateAudio(String text) throws Exception {
        throw new UnsupportedOperationException(
                "El servicio de Text-to-Speech no está disponible actualmente. " +
                        "DeepSeek no ofrece un endpoint de TTS. " +
                        "Por favor, considera usar un servicio alternativo como ElevenLabs, Google TTS, o Amazon Polly.");
    }

}
