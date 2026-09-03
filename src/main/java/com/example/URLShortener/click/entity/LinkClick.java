package com.example.URLShortener.click.entity;

import com.example.URLShortener.link.entity.ShortLink;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "link_clicks")
@Getter
@NoArgsConstructor
public class LinkClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "short_link_id", nullable = false)
    private ShortLink shortLink;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    public LinkClick(ShortLink shortLink) {
        this.shortLink = shortLink;
        this.clickedAt = Instant.now();
    }
}
