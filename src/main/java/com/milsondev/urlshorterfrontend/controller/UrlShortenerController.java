package com.milsondev.urlshorterfrontend.controller;

import com.milsondev.urlshorterfrontend.dtos.UrlCreate;
import com.milsondev.urlshorterfrontend.dtos.UrlGet;
import com.milsondev.urlshorterfrontend.service.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
public class UrlShortenerController {

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlShortenerController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @GetMapping("/")
    public String home(Model model) {
        try {
            model.addAttribute("urls", getUrls());
        } catch (Exception e) {
            model.addAttribute("urls", getUrls());
            model.addAttribute("error", "Failed to load URLs: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/create")
    public ModelAndView createShortUrl(@RequestParam("originalUrl") String originalUrl) {
        ModelAndView mv = new ModelAndView("table-and-form");
        try {
            UrlCreate urlCreate = new UrlCreate(originalUrl);
            urlShortenerService.createShortUrl(urlCreate);
            mv.addObject("success", "URL shortened successfully!");
            mv.addObject("urls", getUrls());
        } catch (Exception e) {
            mv.addObject("urls", getUrls());
            mv.addObject("error", "Failed to shorten URL: " + e.getMessage());
        }
        return mv;
    }

    @GetMapping("/{shortCode}")
    public String redirectToOriginalUrl(@PathVariable String shortCode) {
        try {
            String originalUrl = urlShortenerService.resolveShortCode(shortCode);
            return "redirect:" + originalUrl;
        } catch (HttpClientErrorException.NotFound e) {
            return "redirect:/?error=Short URL not found";
        } catch (Exception e) {
            return "redirect:/?error=" + e.getMessage();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ModelAndView deleteUrl(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        ModelAndView mv = new ModelAndView("table-and-form");
        try {
            urlShortenerService.deleteById(id);
            mv.addObject("success", "URL deleted successfully!");
            mv.addObject("urls", getUrls());
        } catch (Exception e) {
            mv.addObject("urls", getUrls());
            mv.addObject("error", "Failed to delete URL: " + e.getMessage());
        }
        return mv;
    }

    private List<UrlGet> getUrls() {
        return urlShortenerService.findAllPaginated(0, 10);
    }
}