package com.slpixe.springshorturl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
public class UrlRepoTests {

    @Autowired
    private UrlRepo urlRepo;

    @Test
    public void testSaveUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short123");
        urlModel.setFullUrl("https://example.com/full-url");

        UrlModel savedUrlModel = urlRepo.save(urlModel);

        assertNotNull(savedUrlModel.getId());
        assertEquals("short123", savedUrlModel.getShortUrl());
        assertEquals("https://example.com/full-url", savedUrlModel.getFullUrl());
    }

    @Test
    public void testFindByShortUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short456");
        urlModel.setFullUrl("https://example.com/another-url");
        urlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findByShortUrl("short456");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("https://example.com/another-url", foundUrlModel.get().getFullUrl());
    }

    @Test
    public void testFindByFullUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short789");
        urlModel.setFullUrl("https://example.com/yet-another-url");
        urlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findByFullUrl("https://example.com/yet-another-url");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("short789", foundUrlModel.get().getShortUrl());
    }

    @Test
    public void testDeleteUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("shortToDelete");
        urlModel.setFullUrl("https://example.com/delete-me");
        UrlModel savedUrlModel = urlRepo.save(urlModel);

        urlRepo.delete(savedUrlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findById(savedUrlModel.getId());
        assertFalse(foundUrlModel.isPresent());
    }
}
