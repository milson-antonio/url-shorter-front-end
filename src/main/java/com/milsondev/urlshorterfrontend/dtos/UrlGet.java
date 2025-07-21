package com.milsondev.urlshorterfrontend.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UrlGet {

    private final UUID id;
    private String originalUrl;
    private final String shorterUrl;
    private final String description;

    public UrlGet(UUID id, String originalUrl, String shorterUrl, String description) {
        this.id = id;
        this.originalUrl = originalUrl;
        this.shorterUrl = shorterUrl;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShorterUrl() {
        return shorterUrl;
    }

    public String getDescription() {
        return description;
    }
}
