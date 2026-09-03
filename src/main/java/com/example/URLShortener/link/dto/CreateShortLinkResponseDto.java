package com.example.URLShortener.link.dto;

public record CreateShortLinkResponseDto(
        String originalUrl,
        String shortCode,
        String shortUrl
) {
}
