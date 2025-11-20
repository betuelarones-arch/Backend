package com.learning.cliente_app.videogenerator.controller;

import com.learning.cliente_app.videogenerator.services.VideoGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/video")
public class VideoGeneratorController {

    private final VideoGeneratorService videoGeneratorService;

    public VideoGeneratorController(VideoGeneratorService videoGeneratorService) {
        this.videoGeneratorService = videoGeneratorService;
    }

    @PostMapping("/generar")
    public String generarVideo(@RequestBody Map<String, String> body) {
        String prompt = body.get("prompt");
        return videoGeneratorService.generarVideoDesdePrompt(prompt);
    }
}