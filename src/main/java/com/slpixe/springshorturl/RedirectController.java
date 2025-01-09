package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

@Controller
public class RedirectController {

    @Autowired
    private UrlRepo urlRepo;

    @GetMapping("/a")
    public String redirectToGoogle() {
        return "redirect:https://www.google.com";
    }

    @GetMapping("/b/{thing}")
    public String redirectToThing(@PathVariable String thing) {
        return "redirect:https://www.example.com/" + thing;
    }

    @GetMapping("/s/{shortUrl}")
    public String redirectToFullUrl(@PathVariable String shortUrl) {
        return urlRepo.findByShortUrl(shortUrl)
                .map(urlModel -> "redirect:" + urlModel.getFullUrl())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Short URL not found"));
    }
}
