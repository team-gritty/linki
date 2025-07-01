package com.ssg.paymentservice.common.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.paymentservice.common.kafka.event.AutoPaymentSuccessEvent;
import com.ssg.paymentservice.common.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoPaymentSuccessProducer {

    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper objectMapper;

    public void autoSendPaymentSuccessEvent(String topic, AutoPaymentSuccessEvent event) {
        try {
            kafka.send(topic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

