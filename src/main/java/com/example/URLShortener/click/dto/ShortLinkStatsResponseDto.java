package com.example.URLShortener.click.dto;

public record ShortLinkStatsResponseDto(
        String originalUrl,
        String shortCode,
        long clickCount
) {
}
