package com.slpixe.springshorturl;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @Test
    @WithMockUser(username = "testuser")
    public void whenInvalidInput_thenReturnsBadRequest() throws Exception {
        // JSON payload with missing "shortUrl" and "fullUrl" fields
        String invalidRequest = "{ \"shortUrl\": \"\", \"fullUrl\": \"\" }";

        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.error").value(
                        Matchers.anyOf(
                                Matchers.is("shortUrl: Short URL cannot be blank"),
                                Matchers.is("fullUrl: Full URL cannot be blank")
                        )
                ));
    }

    @Test
    public void whenIllegalArgumentException_thenReturnsBadRequest() throws Exception {
        // Step 1: Mock the authenticated user with valid Authentication
        UserModel mockUser = new UserModel(1L, "testuser", "otpSecret");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, List.of()) // Empty authorities list
        );

        // Step 2: Mock UrlService behavior to throw IllegalArgumentException
        doThrow(new IllegalArgumentException("Short URL already exists: duplicate"))
                .when(urlService)
                .createUrl(org.mockito.ArgumentMatchers.any(UrlModel.class), org.mockito.ArgumentMatchers.eq(mockUser));

        // Step 3: Simulate the duplicate shortUrl request
        String duplicateShortUrlRequest = "{ \"shortUrl\": \"duplicate\", \"fullUrl\": \"https://example.com\" }";

        // Step 4: Perform the POST request and validate response
        mockMvc.perform(post("/api/urls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateShortUrlRequest))
                .andExpect(status().isBadRequest()) // Expect 400 Bad Request
                .andExpect(jsonPath("$.error").value("Short URL already exists: duplicate")); // Validate error message
    }

}
