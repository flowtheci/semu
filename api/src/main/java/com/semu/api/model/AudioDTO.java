package com.semu.api.model;

public class AudioDTO {

    private String content;
    private byte[] audio;

    public AudioDTO() {
    }

    public AudioDTO(String content, byte[] audio) {
        this.content = content;
        this.audio = audio;
    }

    public String getContent() {
        return content;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
