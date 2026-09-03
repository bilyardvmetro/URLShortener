package com.example.URLShortener.link;

import com.example.URLShortener.link.entity.ShortLink;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class LinkService {

    private final ShortLinkRepository shortLinkRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;

    public ShortLink generateShortLink(String url) {
        String shortCode = generateUniqueShortCode();

        ShortLink shortLink = new ShortLink(url, shortCode);

        return shortLinkRepository.save(shortLink);
    }

    public ShortLink getLinkByShortCode(String shortCode) {
        return shortLinkRepository.findByShortCode(shortCode).orElseThrow();
    }

    private String generateUniqueShortCode() {
        String code;

        do {
            code = generateShortCode();
        } while (shortLinkRepository.existsByShortCode(code));

        return code;
    }

    private String generateShortCode() {
        StringBuilder builder = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = secureRandom.nextInt(ALPHABET.length());
            builder.append(ALPHABET.charAt(index));
        }

        return builder.toString();
    }
}
