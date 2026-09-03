package com.example.URLShortener.link;

import com.example.URLShortener.link.dto.CreateShortLinkRequestDto;
import com.example.URLShortener.link.dto.CreateShortLinkResponseDto;
import com.example.URLShortener.link.entity.ShortLink;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @PostMapping
    public ResponseEntity<CreateShortLinkResponseDto> createShortLink(
            @Valid @RequestBody CreateShortLinkRequestDto request
    ) {
        ShortLink shortLink = linkService.generateShortLink(request.url());

        return ResponseEntity.ok(new CreateShortLinkResponseDto(
                shortLink.getOriginalUrl(),
                shortLink.getShortCode()
        ));
    }
}
