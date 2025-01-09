package com.slpixe.springshorturl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUrlRepo extends JpaRepository<UrlModel, Long> {

    Optional<UrlModel> findByShortUrl(String shortUrl);

    Optional<UrlModel> findByFullUrl(String fullUrl);
}
