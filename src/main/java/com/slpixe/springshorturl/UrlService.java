package com.slpixe.springshorturl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public List<UrlModel> getUrlsByUser(UserModel user) {
        return urlRepo.findByUser(user);
    }

    public Optional<UrlModel> getUrlByIdAndUser(Long id, UserModel user) {
        return urlRepo.findById(id)
                .filter(url -> url.getUser().getId().equals(user.getId())); // Ensure it belongs to the user
    }

    @Transactional
    public UrlModel updateUrl(Long id, UrlModel updatedUrl, UserModel user) {
        return urlRepo.findById(id)
                .filter(url -> url.getUser().getId().equals(user.getId())) // Validate ownership
                .map(existingUrl -> {
                    existingUrl.setShortUrl(updatedUrl.getShortUrl());
                    existingUrl.setFullUrl(updatedUrl.getFullUrl());
                    return urlRepo.save(existingUrl);
                })
                .orElseThrow(() -> new IllegalArgumentException("URL not found or does not belong to the user"));
    }
}
