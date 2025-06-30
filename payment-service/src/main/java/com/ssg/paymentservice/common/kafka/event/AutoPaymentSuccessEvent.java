package com.ssg.paymentservice.common.kafka.event;

import java.time.LocalDateTime;

public record AutoPaymentSuccessEvent(String userId, LocalDateTime nextBillingAt) {
}
