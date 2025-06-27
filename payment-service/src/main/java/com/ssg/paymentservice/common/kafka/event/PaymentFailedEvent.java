package com.ssg.paymentservice.common.kafka.event;

public record PaymentFailedEvent(String userId,String reason) {
}
