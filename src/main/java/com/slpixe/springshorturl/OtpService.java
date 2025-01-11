package com.slpixe.springshorturl;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

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
        String issuer = "YourAppName"; // Replace with your application's name
        return String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, username, secret, issuer);
    }

}
