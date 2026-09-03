package com.example.URLShortener.click.event;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkClickedEventProducer {

    private final KafkaTemplate<String, LinkClickedEvent> kafkaTemplate;

    @Value("${app.kafka.link-clicked-topic}")
    private String topic;

    public void send(LinkClickedEvent event) {
        kafkaTemplate.send(topic, event.shortCode(), event);
    }
}
