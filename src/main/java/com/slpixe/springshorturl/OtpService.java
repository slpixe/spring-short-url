package com.slpixe.springshorturl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class OtpService {
    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();

    public String generateSecretKey() {
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey(); // Return the secret key
    }

    public boolean validateOtp(String secret, int otp) {
        return gAuth.authorize(secret, otp); // Validate OTP
    }

    public String generateOtpAuthUrl(String username, String secret) {
        String issuer = "SpringShortUrl"; // Replace with your application's name
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer);
    }

    // New method to generate the hosted QR code URL
    public String generateQrCodeUrl(String username, String secret) {
        String otpAuthUrl = generateOtpAuthUrl(username, secret);
        try {
            // URL-encode the OTP Auth URL
            String encodedOtpAuthUrl = URLEncoder.encode(otpAuthUrl, StandardCharsets.UTF_8.toString());
            // Construct the QR code image URL using goqr.me API
            return String.format("https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=%s", encodedOtpAuthUrl);
        } catch (UnsupportedEncodingException e) {
            // Handle the exception as needed
            throw new RuntimeException("Error encoding OTP Auth URL", e);
        }
    }

}
