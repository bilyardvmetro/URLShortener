package com.example.URLShortener.click.event;

import com.example.URLShortener.click.ClickService;
import com.example.URLShortener.link.LinkService;
import com.example.URLShortener.link.entity.ShortLink;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkClickedEventConsumer {

    private final LinkService linkService;
    private final ClickService clickService;

    @KafkaListener(
            topics = "${app.kafka.link-clicked-topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handle(LinkClickedEvent event) {
        ShortLink shortLink = linkService.getLinkByShortCode(event.shortCode());

        clickService.registerClick(shortLink);
    }

}
