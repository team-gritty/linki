//package com.ssg.paymentservice.common.kafka.listner;
//
//
//import com.ssg.paymentservice.common.kafka.event.SubscriptionCreatedEvent;
//import lombok.RequiredArgsConstructor;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//class SubscriptionListener {
//
//    private final PaymentProcessor pay;
//
//    @KafkaListener(topics="subscription.created", groupId="payment-service-group")
//    public void onCreated(SubscriptionCreatedEvent e){
//        pay.firstPayment(e);
//    }
//}
