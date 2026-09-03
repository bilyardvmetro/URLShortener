package com.example.URLShortener.link.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateShortLinkRequestDto(
        @NotBlank
        @Size(max = 2048)
        String url
) {
}
