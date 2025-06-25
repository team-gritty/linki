package com.ssg.paymentservice.common.kafka.event;

import java.time.LocalDateTime;

public record PaymentSuccessEvent(String userId, String subscribeCode, LocalDateTime nextBillingAt) {
}
