package com.learning.cliente_app.podcast.model;

public class VideoResponse {
    private String id;
    private String status;
    private String script;
    private String voice;
    private String audioUrl;
    private String imageUrl;
    private String videoUrl;
    private String message;

    // --- Getters y Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getScript() { return script; }
    public void setScript(String script) { this.script = script; }

    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}