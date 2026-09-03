package com.example.URLShortener.link;

import com.example.URLShortener.link.dto.CreateShortLinkRequestDto;
import com.example.URLShortener.link.dto.CreateShortLinkResponseDto;
import com.example.URLShortener.link.entity.ShortLink;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/links")
@RequiredArgsConstructor
public class LinkController {

    private final LinkService linkService;

    @Value("${app.base-url}")
    private  String baseUrl;

    @PostMapping
    public ResponseEntity<CreateShortLinkResponseDto> createShortLink(
            @Valid @RequestBody CreateShortLinkRequestDto request
    ) {
        ShortLink shortLink = linkService.createShortLink(request.url());

        String shortUrl = baseUrl + "/links" + shortLink.getShortCode();

        return ResponseEntity.ok(new CreateShortLinkResponseDto(
                shortLink.getOriginalUrl(),
                shortLink.getShortCode(),
                shortUrl
        ));
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode
    ) {
        ShortLink shortLink = linkService.getLinkByShortCode(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, shortLink.getOriginalUrl())
                .build();
    }
}
