package com.learning.cliente_app.podcast.model;

public class VideoRequest {
    private String scriptText;
    private String voice;

    public VideoRequest() {}

    public VideoRequest(String scriptText, String voice) {
        this.scriptText = scriptText;
        this.voice = voice;
    }

    public String getScriptText() { return scriptText; }
    public void setScriptText(String scriptText) { this.scriptText = scriptText; }

    public String getVoice() { return voice; }
    public void setVoice(String voice) { this.voice = voice; }
}
