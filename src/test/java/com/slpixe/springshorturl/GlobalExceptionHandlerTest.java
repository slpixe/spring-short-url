package com.slpixe.springshorturl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "testuser")
    public void whenInvalidInput_thenReturnsBadRequest() throws Exception {
        // JSON payload with missing "shortUrl" and "fullUrl" fields
        String invalidRequest = "{ \"shortUrl\": \"\", \"fullUrl\": \"\" }";

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.error").value("fullUrl: Full URL cannot be blank")); // Match the actual error
    }
}
