package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret:#{environment.JWT_SECRET}}")
    private String jwtSecret; // Fetch from application-{profile}.properties or environment variable.

    @Value("${jwt.expiration:3600000}")
    private long jwtExpiration;

    public String getJwtSecret() {
        return jwtSecret;
    }

    public long getJwtExpiration() {
        return jwtExpiration;
    }
}
