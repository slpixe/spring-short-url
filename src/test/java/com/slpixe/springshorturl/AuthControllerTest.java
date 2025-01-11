package com.slpixe.springshorturl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpService otpService;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        userRepo.deleteAll();

        // Create a test user with an OTP secret
        String otpSecret = otpService.generateSecretKey();
        UserModel testUser = new UserModel(null, "testuser", otpSecret);
        userRepo.save(testUser);
    }

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"newuser\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.otpSecret").exists());
    }

    @Test
    void testLogin() throws Exception {
        // Fetch the OTP secret for the test user
        UserModel testUser = userRepo.findByUsername("testuser").orElseThrow();
        int validOtp = new GoogleAuthenticator().getTotpPassword(testUser.getOtpSecret());

        // Perform login with the valid OTP
        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\", \"otp\":\"" + validOtp + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Login successful"));
    }
}
