package com.milsondev.urlshorterfrontend.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Service
@ConditionalOnProperty(value = "url.shortner.api")
public class UrlShortenerService {

		@Value("${url.shortner.api}")
		protected String urlShortnerApi;

		private RestTemplate restTemplate = new RestTemplate();

		private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerService.class);


		public String redirect (final String shortCode ) {
				try {
					String originalUrl = restTemplate.getForObject(urlShortnerApi + "/" + shortCode, String.class);
					return originalUrl;
				}
				catch (final HttpClientErrorException.NotFound ex) {
						// general exception
				}
		}

		public void deleteUrl(final UUID id) {
			final String url = urlShortnerApi + "/" + id;
			restTemplate.delete(url);
		}

}
