package com.example.URLShortener.link;

import com.example.URLShortener.click.ClickService;
import com.example.URLShortener.click.dto.ShortLinkStatsResponseDto;
import com.example.URLShortener.click.event.LinkClickedEvent;
import com.example.URLShortener.click.event.LinkClickedEventProducer;
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
    private final ClickService clickService;
    private final LinkClickedEventProducer linkClickedEventProducer;

    @Value("${app.base-url}")
    private  String baseUrl;

    @PostMapping
    public ResponseEntity<CreateShortLinkResponseDto> createShortLink(
            @Valid @RequestBody CreateShortLinkRequestDto request
    ) {
        ShortLink shortLink = linkService.createShortLink(request.url());

        String shortUrl = baseUrl + "/links/" + shortLink.getShortCode();

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

        linkClickedEventProducer.send(new LinkClickedEvent(
                shortLink.getId(),
                shortLink.getShortCode()
        ));

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, shortLink.getOriginalUrl())
                .build();
    }

    @GetMapping("{shortCode}/stats")
    public ResponseEntity<ShortLinkStatsResponseDto> getStats(
            @PathVariable String shortCode
    ) {
        ShortLink shortLink = linkService.getLinkByShortCode(shortCode);
        long clicks = clickService.countClicks(shortLink);

        return ResponseEntity.ok(new ShortLinkStatsResponseDto(
                shortLink.getOriginalUrl(),
                shortLink.getShortCode(),
                clicks
        ));
    }
}
