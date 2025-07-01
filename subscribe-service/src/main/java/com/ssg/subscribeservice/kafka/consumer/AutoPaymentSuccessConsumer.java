package com.ssg.subscribeservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.subscribeservice.kafka.event.AutoPaymentSuccessEvent;
import com.ssg.subscribeservice.kafka.event.PaymentSuccessEvent;
import com.ssg.subscribeservice.service.AutoPaymentSuccessHandler;
import com.ssg.subscribeservice.service.PaymentSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class AutoPaymentSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final AutoPaymentSuccessHandler autoPaymentSuccessHandler;

    @KafkaListener(topics = "autopayment.success", groupId = "subscription-group")
    public void consumePaymentSuccess(String message) {
        log.info("Consuming auto payment success event: {}", message);
        try {
            AutoPaymentSuccessEvent autopaymentSuccessEvent = objectMapper.readValue(message, AutoPaymentSuccessEvent.class);
            autoPaymentSuccessHandler.handle(autopaymentSuccessEvent);
        } catch (JsonProcessingException e){
             log.error(e.getMessage());
        }
    }
}
