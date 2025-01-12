package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UrlService {

    @Autowired
    private UrlRepo urlRepo;

    public UrlModel createUrl(UrlModel url, UserModel user) {
        // Check if short URL already exists
        Optional<UrlModel> existingUrl = urlRepo.findByShortUrl(url.getShortUrl());
        if (existingUrl.isPresent()) {
            throw new IllegalArgumentException("Short URL already exists: " + url.getShortUrl());
        }

        // Set the user and save the URL
        url.setUser(user);
        return urlRepo.save(url);
    }
}
