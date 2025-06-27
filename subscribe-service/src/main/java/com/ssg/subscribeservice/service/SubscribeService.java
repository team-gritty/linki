package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;

public interface SubscribeService {
    boolean isBillingRegistered(String userId);
    SubscriptionCreatedEvent createSubscription(String userId, String role);
}
