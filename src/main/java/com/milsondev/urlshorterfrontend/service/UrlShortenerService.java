package com.milsondev.urlshorterfrontend.service;


import com.milsondev.urlshorterfrontend.dtos.UrlCreate;
import com.milsondev.urlshorterfrontend.dtos.UrlGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import org.springframework.http.HttpStatus;

import java.util.List;

@Service
@ConditionalOnProperty(value = "url.shortner.api")
public class UrlShortenerService {

	@Value("${url.shortner.api}")
	protected String urlShortnerApi;

	@Value("${host.name:http://localhost:8080}")
	protected String hostName;

	private final RestTemplate restTemplate = new RestTemplate();

	private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerService.class);

	public void createShortUrl(UrlCreate urlCreate) {
		try {
			LOGGER.info("Creating short URL for: {}", urlCreate.getOriginalUrl());
			String url = urlShortnerApi;
			UrlGet response = restTemplate.postForObject(url, urlCreate, UrlGet.class);
			if (response == null) {
				LOGGER.error("Failed to create short URL: API returned null");
				throw new RuntimeException("Failed to create short URL");
			}
			LOGGER.info("Successfully created short URL: {}", response.getShorterUrl());
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error creating short URL: {}", ex.getMessage());
			throw new RuntimeException("Failed to create short URL: " + ex.getMessage(), ex);
		}
	}

	public String resolveShortCode(String shortCode) {
		try {
			LOGGER.info("Resolving short code: {}", shortCode);
			String originalUrl = restTemplate.getForObject(urlShortnerApi + "/" + shortCode, String.class);
			if (originalUrl == null) {
				LOGGER.error("Short code not found: {}", shortCode);
				throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Short code not found");
			}
			LOGGER.info("Resolved short code {} to: {}", shortCode, originalUrl);
			return originalUrl;
		} catch (HttpClientErrorException.NotFound ex) {
			LOGGER.error("Short code not found: {}", shortCode);
			throw new HttpClientErrorException(HttpStatus.NOT_FOUND, "Short code not found");
		} catch (Exception ex) {
			LOGGER.error("Error resolving short code: {}", ex.getMessage());
			throw new RuntimeException("Failed to resolve short code: " + ex.getMessage(), ex);
		}
	}

	public void deleteById(UUID id) {
		try {
			LOGGER.info("Deleting URL with ID: {}", id);
			final String url = urlShortnerApi + "/" + id;
			restTemplate.delete(url);
			LOGGER.info("Successfully deleted URL with ID: {}", id);
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error deleting URL with ID {}: {}", id, ex.getMessage());
			throw new RuntimeException("Failed to delete URL: " + ex.getMessage(), ex);
		}
	}

	public List<UrlGet> findAllPaginated(int page, int size) {
		try {
			LOGGER.info("Fetching paginated URLs: page={}, size={}", page, size);
			String url = urlShortnerApi + "?page=" + page + "&size=" + size;
			UrlGet[] response = restTemplate.getForObject(url, UrlGet[].class);
			if (response == null) {
				LOGGER.error("No URLs found for page={} and size={}", page, size);
				return List.of();
			}

			List<UrlGet> modifiedResponse = new java.util.ArrayList<>();
			for (UrlGet urlGet : response) {
				UrlGet modifiedUrlGet = new UrlGet(
					urlGet.getId(),
					urlGet.getOriginalUrl(),
					hostName + "/" + urlGet.getShorterUrl(),
					urlGet.getDescription()
				);
				modifiedResponse.add(modifiedUrlGet);
			}

			LOGGER.info("Successfully fetched {} URLs", response.length);
			return modifiedResponse;
		} catch (HttpClientErrorException ex) {
			LOGGER.error("Error fetching paginated URLs: {}", ex.getMessage());
			throw new RuntimeException("Failed to fetch URLs: " + ex.getMessage(), ex);
		}
	}
}
