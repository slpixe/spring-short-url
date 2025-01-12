package com.slpixe.springshorturl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This test class assumes that the SecurityConfig (or equivalent) allows requests
 * to reach the controller so that the controller's own checks for a null user
 * (returning 401) can be tested. If Spring Security blocks unauthenticated requests
 * before they reach the controller, you'd see 403 (Forbidden) instead of 401 (Unauthorized).
 *
 * If you're seeing 403 from Spring Security, either configure /api/urls/** to permitAll()
 * so that the controller can handle unauthenticated requests, or set a custom
 * AuthenticationEntryPoint to explicitly send 401.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepo urlRepo;

    @Autowired
    private UserRepo userRepo;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        urlRepo.deleteAll();
        userRepo.deleteAll();
        testUser = new UserModel(null, "testuser", "dummy_secret");
        testUser = userRepo.save(testUser);
        SecurityContextHolder.clearContext(); // clear any previous auth
    }

    @Test
    void testGetUserUrlsReturns401WhenNullUser() throws Exception {
        // No auth set -> controller should detect user == null and return 401
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testGetUserUrlsAuthorized() throws Exception {
        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // Should return 200 and initially an empty array
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testCreateUrlReturns401WhenNullUser() throws Exception {
        // No auth set -> controller should detect user == null and return 401
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"shortUrl\":\"myShort\",\"fullUrl\":\"https://example.com\"}")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testCreateUrlSuccessfully() throws Exception {
        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"myShort\", \"fullUrl\": \"https://example.com\" }")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.shortUrl").value("myShort"))
                .andExpect(jsonPath("$.fullUrl").value("https://example.com"));
    }

    @Test
    void testCreateUrlDuplicateShortUrl() throws Exception {
        // Create a URL in the repo for the same user
        UrlModel existingUrl = new UrlModel(null, "dupShort", "https://example.org", testUser);
        urlRepo.save(existingUrl);

        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"dupShort\", \"fullUrl\": \"https://example.com/new\" }")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Short URL already exists: dupShort"));
    }

    @Test
    void testUpdateUrlReturns401WhenNullUser() throws Exception {
        // No auth -> expect controller to detect user == null and return 401
        mockMvc.perform(put("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"updateShort\", \"fullUrl\": \"https://example.com/update\" }")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testUpdateUrlAuthorized() throws Exception {
        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // The controller currently returns a placeholder message
        mockMvc.perform(put("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"updateShort\", \"fullUrl\": \"https://example.com/update\" }")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("Updating a URL is not yet implemented."));
    }

    @Test
    void testDeleteUrlReturns401WhenNullUser() throws Exception {
        // No auth -> expect 401
        mockMvc.perform(delete("/api/urls/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testDeleteUrlAuthorized() throws Exception {
        // Authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // The controller currently returns a placeholder message
        mockMvc.perform(delete("/api/urls/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleting a URL is not yet implemented."));
    }
}
