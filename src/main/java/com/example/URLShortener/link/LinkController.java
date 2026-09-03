package com.example.URLShortener.link;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/links")
public class LinkController {

    @PostMapping
    public ResponseEntity<?> createShortLink(
            @Valid @RequestBody CreateShortLinkRequestDto request
    ) {
        String url = request.url();
        // тут будет логика
        return ResponseEntity.ok(url);
    }
}
