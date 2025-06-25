package com.ssg.subscribeservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionProducer {

    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper objectMapper;

    //구독 요청 생성
    void sendCreated(String topic, SubscriptionCreatedEvent event) {

        try {
            kafka.send("subscription.created", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
