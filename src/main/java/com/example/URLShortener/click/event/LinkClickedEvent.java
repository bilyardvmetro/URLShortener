package com.example.URLShortener.click.event;

import com.example.URLShortener.link.entity.ShortLink;

public record LinkClickedEvent(
        Long shortLinkId,
        String shortCode
) {
}
