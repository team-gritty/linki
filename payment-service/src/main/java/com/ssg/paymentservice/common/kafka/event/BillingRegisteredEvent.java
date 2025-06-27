package com.ssg.paymentservice.common.kafka.event;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

public record BillingRegisteredEvent(String userId) {}
