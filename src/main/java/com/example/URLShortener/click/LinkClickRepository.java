package com.example.URLShortener.click;

import com.example.URLShortener.click.entity.LinkClick;
import com.example.URLShortener.link.entity.ShortLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {
    long countByShortLink(ShortLink shortLink);
}
