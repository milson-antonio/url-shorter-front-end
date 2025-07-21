package com.milsondev.urlshorterfrontend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class UrlCreate {
    @NotNull(message = "The URL must not be null.")
    @NotBlank(message = "The URL must not be empty.")
    @Pattern(regexp = "^(https?://).+", message = "The URL must start with http:// or https://")
    private final String originalUrl;

    private final String description;

    public UrlCreate(String originalUrl, String description) {
        this.originalUrl = originalUrl;
        this.description = description;
    }

    public UrlCreate(String originalUrl) {
        this(originalUrl, null);
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getDescription() {
        return description;
    }

}
