package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.kafka.event.AutoPaymentSuccessEvent;

public interface AutoPaymentSuccessHandler {
    UserSubscribeDto handle(AutoPaymentSuccessEvent autoPaymentSuccessEvent);
}
