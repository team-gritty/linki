package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.kafka.event.BillingRegisteredEvent;

public interface BillingRegisteredHandler {
    void handle(BillingRegisteredEvent event);
}
