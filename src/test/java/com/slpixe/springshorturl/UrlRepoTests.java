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
    private UrlRepo urlRepo;

    @Autowired
    private UserRepo userRepo;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        urlRepo.deleteAll();
        userRepo.deleteAll();

        // Create and save a test user
        testUser = new UserModel(null, "testuser", "dummy_secret");
        testUser = userRepo.save(testUser);
    }

    @Test
    public void testSaveUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short111");
        urlModel.setFullUrl("https://example.com/full-url");
        urlModel.setUser(testUser); // Associate with the test user

        UrlModel savedUrlModel = urlRepo.save(urlModel);

        assertNotNull(savedUrlModel.getId());
        assertEquals("short111", savedUrlModel.getShortUrl());
        assertEquals("https://example.com/full-url", savedUrlModel.getFullUrl());
        assertEquals(testUser.getId(), savedUrlModel.getUser().getId());
    }

    @Test
    public void testFindByShortUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short222");
        urlModel.setFullUrl("https://example.com/another-url");
        urlModel.setUser(testUser); // Associate with the test user
        urlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findByShortUrl("short222");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("https://example.com/another-url", foundUrlModel.get().getFullUrl());
        assertEquals(testUser.getId(), foundUrlModel.get().getUser().getId());
    }

    @Test
    public void testFindByFullUrl() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("short789");
        urlModel.setFullUrl("https://example.com/yet-another-url");
        urlModel.setUser(testUser); // Associate with the test user
        urlRepo.save(urlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findByFullUrl("https://example.com/yet-another-url");

        assertTrue(foundUrlModel.isPresent());
        assertEquals("short789", foundUrlModel.get().getShortUrl());
        assertEquals(testUser.getId(), foundUrlModel.get().getUser().getId());
    }

    @Test
    public void testDeleteUrlModel() {
        UrlModel urlModel = new UrlModel();
        urlModel.setShortUrl("shortToDelete");
        urlModel.setFullUrl("https://example.com/delete-me");
        urlModel.setUser(testUser); // Associate with the test user

        UrlModel savedUrlModel = urlRepo.save(urlModel);
        urlRepo.delete(savedUrlModel);

        Optional<UrlModel> foundUrlModel = urlRepo.findById(savedUrlModel.getId());

        assertFalse(foundUrlModel.isPresent());
    }
}
