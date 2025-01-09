package com.slpixe.springshorturl;

import org.junit.jupiter.api.BeforeEach;
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
    private UrlRepo UrlRepo;

    @BeforeEach
    void cleanDatabase() {
        UrlRepo.deleteAll();
    }

    @Test
    public void testSaveUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short123");
        urlModel.setFullUrl("https://example.com/full-url");

        UrlModel savedUrlModel = UrlRepo.save(urlModel);

        assertNotNull(savedUrlModel.getId());
        assertEquals("short123", savedUrlModel.getShortUrl());
        assertEquals("https://example.com/full-url", savedUrlModel.getFullUrl());
    }

    @Test
    public void testFindByShortUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short456");
        urlModel.setFullUrl("https://example.com/another-url");
        UrlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = UrlRepo.findByShortUrl("short456");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("https://example.com/another-url", foundUrlModel.get().getFullUrl());
    }

    @Test
    public void testFindByFullUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short789");
        urlModel.setFullUrl("https://example.com/yet-another-url");
        UrlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = UrlRepo.findByFullUrl("https://example.com/yet-another-url");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("short789", foundUrlModel.get().getShortUrl());
    }

    @Test
    public void testDeleteUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("shortToDelete");
        urlModel.setFullUrl("https://example.com/delete-me");
        UrlModel savedUrlModel = UrlRepo.save(urlModel);

        UrlRepo.delete(savedUrlModel);

        Optional<UrlModel> foundUrlModel = UrlRepo.findById(savedUrlModel.getId());
        assertFalse(foundUrlModel.isPresent());
    }
}
