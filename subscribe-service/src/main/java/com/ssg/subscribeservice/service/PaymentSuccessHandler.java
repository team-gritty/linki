package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.kafka.event.PaymentSuccessEvent;

public interface PaymentSuccessHandler {
    UserSubscribeDto handle(PaymentSuccessEvent paymentSuccessEvent);
}
