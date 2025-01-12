package com.slpixe.springshorturl;

import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepo urlRepo;

    @Test
    public void whenValidInput_thenReturnsCreatedUrl() throws Exception {
        // Step 1: Mock the authenticated user
        UserModel mockUser = new UserModel(1L, "testuser", "otpSecret");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, List.of())
        );

        // Step 2: Valid request payload
        String validRequest = "{ \"shortUrl\": \"validShort\", \"fullUrl\": \"https://example.com/valid-url\" }";

        // Step 3: Perform the POST request and validate response
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value("validShort"))
                .andExpect(jsonPath("$.fullUrl").value("https://example.com/valid-url"))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }
}
