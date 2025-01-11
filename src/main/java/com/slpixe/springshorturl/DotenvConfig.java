package com.slpixe.springshorturl;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DotenvConfig {

    private final Dotenv dotenv;

    public DotenvConfig() {
        // .env is loaded automatically from the project root by default
        this.dotenv = Dotenv.configure()
                .ignoreIfMissing()  // won't fail if no .env is found
                .load();
    }

    public String getJwtSecret() {
        // fallback if JWT_SECRET is missing
        return dotenv.get("JWT_SECRET", "defaultSecretKey");
    }

    public long getJwtExpiration() {
        // fallback if JWT_EXPIRATION is missing
        String expiration = dotenv.get("JWT_EXPIRATION", "3600000");
        return Long.parseLong(expiration);
    }
}
