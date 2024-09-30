package com.elite_gear_backend.dto;

public class PhotoDto {
    private String url;

    public PhotoDto(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}