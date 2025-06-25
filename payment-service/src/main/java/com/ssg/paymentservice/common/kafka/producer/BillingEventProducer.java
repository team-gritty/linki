package com.ssg.paymentservice.common.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.paymentservice.common.kafka.event.BillingRegisteredEvent;
import com.ssg.paymentservice.common.kafka.event.PaymentFailedEvent;
import com.ssg.paymentservice.common.kafka.event.PaymentSuccessEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillingEventProducer {

    private final KafkaTemplate<String,String> kafka;
    private final ObjectMapper objectMapper;

    public void billingRegistered(String topic, BillingRegisteredEvent event) {
        try {
            kafka.send(topic, objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
//    public void paymentSuccess(PaymentSuccessEvent e){
//        kafka.send("payment.success", e.userId(), e);
//    }
//    public void paymentFailed(PaymentFailedEvent e){
//        kafka.send("payment.failed", e.userId(), e);
//    }
