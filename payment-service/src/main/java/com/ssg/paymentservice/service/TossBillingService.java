package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.kafka.event.SubscriptionCreatedEvent;
import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.responsedto.BillingKeyResponseDto;

import java.util.List;

public interface TossBillingService {
    //빌링키 요청
    BillingKeyResponseDto confirmBilling(String authKey, String customerKey);
    void firstPayment(String userId, SubscriptionCreatedEvent subscriptionCreatedEvent);
    List<BillingDto> processAutoBilling(List<BillingDto> billingDtoList);
}
