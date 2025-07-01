package com.ssg.subscribeservice.kafka.event;

import java.time.LocalDateTime;

public record AutoPaymentSuccessEvent(String userId, LocalDateTime nextBillingAt) {
}
