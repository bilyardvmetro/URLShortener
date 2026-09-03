package com.example.URLShortener.link;

import com.example.URLShortener.link.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface ShortLinkRepository extends JpaRepository<ShortLink, Long> {

    Optional<ShortLink> findByShortCode(String shortCode);

    Optional<ShortLink> findByOriginalUrl(String originalUrl);

    boolean existsByShortCode(String shortCode);

}
