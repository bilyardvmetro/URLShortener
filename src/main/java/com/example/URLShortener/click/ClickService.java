package com.example.URLShortener.click;

import com.example.URLShortener.click.entity.LinkClick;
import com.example.URLShortener.link.entity.ShortLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClickService {

    private final LinkClickRepository linkClickRepository;

    public void registerClick(ShortLink shortLink) {
        linkClickRepository.save(new LinkClick(shortLink));
    }

    public long countClicks(ShortLink shortLink) {
        return linkClickRepository.countByShortLink(shortLink);
    }
}
