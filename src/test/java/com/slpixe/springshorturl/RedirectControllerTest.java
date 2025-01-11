package com.slpixe.springshorturl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlRepo urlRepo;

    @Autowired
    private UserRepo userRepo;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        urlRepo.deleteAll();
        userRepo.deleteAll();

        // Create a test user
        testUser = new UserModel(null, "testuser", "dummy_secret");
        testUser = userRepo.save(testUser);
    }

    @Test
    void shouldRedirectToGoogle() throws Exception {
        mockMvc.perform(get("/a"))
                .andExpect(status().is3xxRedirection()) // Expect a 3xx redirection status
                .andExpect(redirectedUrl("https://www.google.com")); // Expect redirection to google.com
    }

    @Test
    void shouldRedirectToThing() throws Exception {
        mockMvc.perform(get("/b/aaa"))
                .andExpect(status().is3xxRedirection()) // Expect a 3xx redirection status
                .andExpect(redirectedUrl("https://www.example.com/aaa")); // Expect redirection to the correct URL
    }

    @Test
    void testShortUrlNotFoundAndThenFound() throws Exception {
        // Step 1: Test that /s/111 returns 404
        mockMvc.perform(get("/s/111"))
                .andExpect(status().isNotFound()); // Expect a 404 Not Found

        // Step 2: Add a short URL to the repository
        UrlModel urlModel = new UrlModel(null, "111", "https://example.com/redirect-url", testUser);
        urlRepo.save(urlModel);

        // Step 3: Test that /s/111 now redirects to the full URL
        mockMvc.perform(get("/s/111"))
                .andExpect(status().is3xxRedirection()) // Expect a 3xx redirection status
                .andExpect(redirectedUrl("https://example.com/redirect-url")); // Expect redirection to the added URL
    }

    @Test
    void testRedirectToFullUrl() throws Exception {
        // Step 1: Test with a valid short URL
        UrlModel urlModel = new UrlModel(null, "validShort", "https://example.com/valid-url", testUser);
        urlRepo.save(urlModel);

        mockMvc.perform(get("/s/validShort"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("https://example.com/valid-url"));

        // Step 2: Test with a non-existing short URL
        mockMvc.perform(get("/s/nonExisting"))
                .andExpect(status().isNotFound());

        // Step 3: Test with an empty short URL
        mockMvc.perform(get("/s/"))
                .andExpect(status().isNotFound());
    }
}
