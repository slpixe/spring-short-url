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
    }

    @Test
    void testGetUserUrlsUnauthorized() throws Exception {
        // No SecurityContext authentication set
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testGetUserUrlsAuthorized() throws Exception {
        // Manually set an authenticated user in the SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // Verify that we get a 200 OK and (initially) an empty list
        mockMvc.perform(get("/api/urls"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]")); // expecting empty array
    }

    @Test
    void testCreateUrlUnauthorized() throws Exception {
        // Not setting authentication to simulate unauthorized user
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"myShort\", \"fullUrl\": \"https://example.com\" }")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testCreateUrlSuccessfully() throws Exception {
        // Manually set an authenticated user
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

        // Authenticate as testUser
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // Try to create another URL with the same shortUrl
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"dupShort\", \"fullUrl\": \"https://example.com/new\" }")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Short URL already exists: dupShort"));
    }

    @Test
    void testUpdateUrlUnauthorized() throws Exception {
        mockMvc.perform(put("/api/urls/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"shortUrl\": \"updateShort\", \"fullUrl\": \"https://example.com/update\" }")
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testUpdateUrlAuthorized() throws Exception {
        // Manually set authenticated user
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
    void testDeleteUrlUnauthorized() throws Exception {
        mockMvc.perform(delete("/api/urls/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("User not authenticated"));
    }

    @Test
    void testDeleteUrlAuthorized() throws Exception {
        // Manually set authenticated user
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(testUser, null, List.of())
        );

        // The controller currently returns a placeholder message
        mockMvc.perform(delete("/api/urls/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleting a URL is not yet implemented."));
    }
}
