package com.ssg.paymentservice.common.kafka.event;

//구독 요청시 결제서버에 이벤트 발행˜
//Topic: subscription.created
public record SubscriptionCreatedEvent(
        String userId,
        String userName,
        String userEmail,
        Integer subscribeAmount,
        String subscribeId,
        String subscribeName
){}
