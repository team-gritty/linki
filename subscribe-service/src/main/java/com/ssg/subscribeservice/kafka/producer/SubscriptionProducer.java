package com.ssg.subscribeservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionProducer {

    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper objectMapper;

    //구독 요청 생성
    public void sendCreated(String topic, SubscriptionCreatedEvent event) {
        try {
            log.info("Created subscription: {}", event);
            kafka.send(topic, objectMapper.writeValueAsString(event));
            log.info("Created subscription: {}", event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
