package com.ssg.subscribeservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.SubscribeSuccessEvent;
import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscribeSuccessProducer {

    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper objectMapper;

    //구독 요청 생성
    public void sendSuccess(String topic, SubscribeSuccessEvent event) {
        try {
            log.info("Success Subscribe: {}", event);
            kafka.send(topic, objectMapper.writeValueAsString(event));
            log.info("Success Subscribe: {}", event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
