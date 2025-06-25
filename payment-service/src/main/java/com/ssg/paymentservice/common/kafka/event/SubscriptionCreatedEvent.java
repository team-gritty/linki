package com.ssg.paymentservice.common.kafka.event;

public record SubscriptionCreatedEvent(String userId, String subscribeId) {
}
