package com.learning.cliente_app.lecciones.service;

import com.learning.cliente_app.lecciones.dto.PreguntaDTO;
import com.learning.cliente_app.lecciones.dto.PreguntaUpdateDTO;
import com.learning.cliente_app.lecciones.model.Pregunta;
import com.learning.cliente_app.lecciones.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;
    private final ExtractorTextoService extractorTextoService;
    private final GeneradorPreguntasService generadorPreguntasService;

    public PreguntaService(PreguntaRepository preguntaRepository,
                           ExtractorTextoService extractorTextoService,
                           GeneradorPreguntasService generadorPreguntasService) {
        this.preguntaRepository = preguntaRepository;
        this.extractorTextoService = extractorTextoService;
        this.generadorPreguntasService = generadorPreguntasService;
    }

    /**
     * Procesa un archivo, extrae texto, genera preguntas y las guarda
     */
    @Transactional
    public List<Pregunta> procesarArchivoYGenerarPreguntas(MultipartFile file, int numPreguntas) throws Exception {

        String nombreArchivo = file.getOriginalFilename();

        // 1. Extraer texto del archivo
        String textoExtraido = extractorTextoService.extraerTexto(file);

        if (textoExtraido == null || textoExtraido.trim().isEmpty()) {
            throw new RuntimeException("No se pudo extraer texto del archivo");
        }

        // 2. Generar preguntas con IA
        List<PreguntaDTO> preguntasDTO = generadorPreguntasService.generarPreguntas(textoExtraido, numPreguntas);

        // 3. Convertir DTOs a entidades y guardar
        List<Pregunta> preguntas = preguntasDTO.stream()
                .map(dto -> new Pregunta(
                        nombreArchivo,
                        dto.getPregunta(),
                        dto.getOpciones(),
                        dto.getRespuestaCorrecta()
                ))
                .collect(Collectors.toList());

        return preguntaRepository.saveAll(preguntas);
    }

    /**
     * Obtener todas las preguntas
     */
    public List<Pregunta> obtenerTodasLasPreguntas() {
        return preguntaRepository.findAll();
    }

    /**
     * Obtener preguntas por archivo
     */
    public List<Pregunta> obtenerPreguntasPorArchivo(String nombreArchivo) {
        return preguntaRepository.findByArchivoOrigen(nombreArchivo);
    }

    /**
     * Eliminar preguntas por archivo
     */
    @Transactional
    public void eliminarPreguntasPorArchivo(String nombreArchivo) {
        preguntaRepository.deleteByArchivoOrigen(nombreArchivo);
    }

    @Transactional
    public Pregunta actualizarPregunta(Long id, PreguntaUpdateDTO updateDTO) {
        Pregunta pregunta = preguntaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada con ID: " + id));

        // Actualizar solo los campos que no son null
        if (updateDTO.getPregunta() != null) {
            pregunta.setPregunta(updateDTO.getPregunta());
        }

        if (updateDTO.getOpciones() != null) {
            pregunta.setOpcionesLista(updateDTO.getOpciones());
        }

        if (updateDTO.getRespuestaCorrecta() != null) {
            pregunta.setRespuestaCorrecta(updateDTO.getRespuestaCorrecta());
        }

        return preguntaRepository.save(pregunta);
    }

    @Transactional
    public void eliminarPreguntaPorId(Long id) {
        if (!preguntaRepository.existsById(id)) {
            throw new RuntimeException("Pregunta no encontrada con ID: " + id);
        }
        preguntaRepository.deleteById(id);
    }
}