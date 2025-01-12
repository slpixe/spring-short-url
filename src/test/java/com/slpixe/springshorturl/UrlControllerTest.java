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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void testCreateUrlSuccessfully() throws Exception {
        // Manually set an authenticated user in the SecurityContext
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
}
