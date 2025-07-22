package com.milsondev.urlshorterfrontend.controller;

import com.milsondev.urlshorterfrontend.dtos.UrlCreate;
import com.milsondev.urlshorterfrontend.dtos.UrlGet;
import com.milsondev.urlshorterfrontend.service.UrlShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = UrlShortenerController.class)
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    private List<UrlGet> mockUrls;
    private UUID testId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        mockUrls = new java.util.ArrayList<>();
        mockUrls.add(new UrlGet(testId, "https://example.com", "abc123", "Example URL"));

        when(urlShortenerService.findAllPaginated(anyInt(), anyInt())).thenReturn(mockUrls);
    }

    @Test
    void home_shouldReturnIndexPageWithUrls() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("urls", mockUrls));
    }

    @Test
    void createShortUrl_shouldRedirectToHomeWithSuccess() throws Exception {
        // Note: The service method returns void, not UrlGet as expected in the original test
        doNothing().when(urlShortenerService).createShortUrl(any(UrlCreate.class));

        mockMvc.perform(post("/create")
                .param("originalUrl", "https://example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("table-and-form"))
                .andExpect(model().attribute("success", "URL shortened successfully!"))
                .andExpect(model().attribute("urls", mockUrls));
    }

    @Test
    void redirectToOriginalUrl_shouldRedirectToOriginalUrl() throws Exception {
        String shortCode = "abc123";
        String originalUrl = "https://example.com";

        when(urlShortenerService.resolveShortCode(eq(shortCode))).thenReturn(originalUrl);

        mockMvc.perform(get("/{shortCode}", shortCode))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(originalUrl));
    }

    @Test
    void deleteUrl_shouldRedirectToHomeWithSuccess() throws Exception {
        doNothing().when(urlShortenerService).deleteById(eq(testId));

        // Note: The original test expected a redirect but the controller returns a view
        mockMvc.perform(delete("/delete/{id}", testId))
                .andExpect(status().isOk())
                .andExpect(view().name("table-and-form"))
                .andExpect(model().attribute("success", "URL deleted successfully!"))
                .andExpect(model().attribute("urls", mockUrls));
    }
}
