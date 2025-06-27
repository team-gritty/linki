package com.ssg.subscribeservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.PaymentSuccessEvent;
import com.ssg.subscribeservice.service.PaymentSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentSuccessHandler paymentSuccessHandler;

    @KafkaListener(topics = "payment.success", groupId = "subscription-group")
    public void consumePaymentSuccess(String message) {
        log.info("Consuming payment success event: {}", message);
        try {
            PaymentSuccessEvent paymentSuccessEvent = objectMapper.readValue(message, PaymentSuccessEvent.class);
            paymentSuccessHandler.handle(paymentSuccessEvent);
        } catch (JsonProcessingException e){
             log.error(e.getMessage());
        }
    }
}
