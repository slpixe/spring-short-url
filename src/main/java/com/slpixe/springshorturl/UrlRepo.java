package com.slpixe.springshorturl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface UrlRepo extends JpaRepository<UrlModel, Long> {

    Optional<UrlModel> findByShortUrl(String shortUrl);

    Optional<UrlModel> findByFullUrl(String fullUrl);

    List<UrlModel> findByUser(UserModel user);
}
