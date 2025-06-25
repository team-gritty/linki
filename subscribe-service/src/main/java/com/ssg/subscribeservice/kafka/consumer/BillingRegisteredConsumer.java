package com.ssg.subscribeservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.BillingRegisteredEvent;
import com.ssg.subscribeservice.service.BillingRegisteredHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class BillingRegisteredConsumer {

    private final ObjectMapper objectMapper;
    private final BillingRegisteredHandler billingRegisteredHandler;

    @KafkaListener(topics = "billing.registered", groupId = "subscribe-service-group")
    public void consumeBillingRegistered(String message) {
        log.info("Consuming billing registered event: {}", message);
        try {
            BillingRegisteredEvent billingRegisteredEvent = objectMapper.readValue(message, BillingRegisteredEvent.class);
            billingRegisteredHandler.handle(billingRegisteredEvent);
        } catch (JsonProcessingException e){
             log.error(e.getMessage());
        }
    }
}
