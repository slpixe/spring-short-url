package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
public class UrlController {
    @Autowired
    private UrlRepo urlRepo;

    @GetMapping
    public List<UrlModel> getUserUrls(@AuthenticationPrincipal UserModel user) {
        return urlRepo.findByUser(user); // Fetch URLs owned by the logged-in user
    }

    @PostMapping
    public UrlModel createUrl(@AuthenticationPrincipal UserModel user, @RequestBody UrlModel url) {

        System.out.println("Controller - SecurityContext: " +
                SecurityContextHolder.getContext().getAuthentication());

        if (user == null) {
            throw new IllegalStateException("Authenticated user is null");
        }
        System.out.println("url = " + url);
        System.out.println("user = " + user);
        url.setUser(user); // Set the owner of the URL
        return urlRepo.save(url);
    }

    @PutMapping("/{id}")
    public UrlModel updateUrl(@AuthenticationPrincipal UserModel user, @PathVariable Long id, @RequestBody UrlModel updatedUrl) {
        UrlModel existingUrl = urlRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));
        if (!existingUrl.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit this URL");
        }
        existingUrl.setFullUrl(updatedUrl.getFullUrl());
        return urlRepo.save(existingUrl);
    }

    @DeleteMapping("/{id}")
    public void deleteUrl(@AuthenticationPrincipal UserModel user, @PathVariable Long id) {
        UrlModel existingUrl = urlRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "URL not found"));
        if (!existingUrl.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this URL");
        }
        urlRepo.delete(existingUrl);
    }
}

