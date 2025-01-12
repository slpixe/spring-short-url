package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    @Autowired
    private UrlService urlService;

    @GetMapping
    public ResponseEntity<?> getUserUrls(@AuthenticationPrincipal UserModel user) {
        // Placeholder for fetching user URLs
        return ResponseEntity.ok("Fetching user URLs is not yet implemented.");
    }

    @PostMapping
    public ResponseEntity<?> createUrl(@AuthenticationPrincipal UserModel user, @RequestBody UrlModel url) {
        try {
            UrlModel createdUrl = urlService.createUrl(url, user);
            return ResponseEntity.ok(createdUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUrl(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long id,
            @RequestBody UrlModel updatedUrl
    ) {
        // Placeholder for updating a URL
        return ResponseEntity.ok("Updating a URL is not yet implemented.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUrl(
            @AuthenticationPrincipal UserModel user,
            @PathVariable Long id
    ) {
        // Placeholder for deleting a URL
        return ResponseEntity.ok("Deleting a URL is not yet implemented.");
    }
}
