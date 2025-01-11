package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userDetails) {
        String username = userDetails.get("username");
        if (userRepo.findByUsername(username).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User already exists");
        }

        // Generate OTP secret
        String otpSecret = otpService.generateSecretKey();
        UserModel user = new UserModel();
        user.setUsername(username);
        user.setOtpSecret(otpSecret);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "User registered successfully",
                "otpSecret", otpSecret
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginDetails) {
        String username = loginDetails.get("username");
        int otp = Integer.parseInt(loginDetails.get("otp"));

        UserModel user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!otpService.validateOtp(user.getOtpSecret(), otp)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid OTP");
        }

        // Generate JWT or handle session creation
        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }
}
