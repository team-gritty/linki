package com.ssg.paymentservice.common.kafka.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.paymentservice.common.kafka.event.SubscriptionCreatedEvent;
import com.ssg.paymentservice.service.TossBillingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCreatedConsumer {

    private final ObjectMapper objectMapper;
    private final TossBillingService tossBillingService;


    @KafkaListener(topics = "subscription.created", groupId = "payment-group")
    public void consumeBillingRegistered(String message) {
        log.info("Consuming subscribe.created event: {}", message);
        try {
            SubscriptionCreatedEvent subscriptionCreatedEvent = objectMapper.readValue(message, SubscriptionCreatedEvent.class);
            tossBillingService.firstPayment(subscriptionCreatedEvent.userId(), subscriptionCreatedEvent);
        } catch (JsonProcessingException e){
            log.error(e.getMessage());
        }
    }
}
