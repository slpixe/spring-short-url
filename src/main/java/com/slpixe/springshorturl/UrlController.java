package com.slpixe.springshorturl;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
        // Validate the user in the controller
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not authenticated"));
        }

        // Fetch the URLs from the service
        List<UrlModel> userUrls = urlService.getUrlsByUser(user);
        return ResponseEntity.ok(userUrls);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUrlById(@AuthenticationPrincipal UserModel user, @PathVariable Long id) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not authenticated"));
        }

        return urlService.getUrlByIdAndUser(id, user)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("URL not found or does not belong to the user"))); // Error response
    }

    @PostMapping
    public ResponseEntity<?> createUrl(
            @AuthenticationPrincipal UserModel user,
            @RequestBody @Valid UrlModel url
    ) {
        // Validate the authenticated user
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not authenticated"));
        }

        try {
            // Delegate to the service for business logic
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
            @RequestBody @Valid UrlModel updatedUrl
    ) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("User not authenticated"));
        }

        try {
            UrlModel updated = urlService.updateUrl(id, updatedUrl, user);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
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
