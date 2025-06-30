package com.ssg.paymentservice.common.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaTopicConfig {
    private NewTopic topic(String name) {
        return TopicBuilder.name(name).build();
    }
    @Bean NewTopic billingRegistered()   { return topic("billing.registered"); }
    @Bean NewTopic subscriptionCreated() { return topic("subscription.created"); }
    @Bean NewTopic paymentSuccess()      { return topic("payment.success"); }
    @Bean NewTopic paymentFailed()       { return topic("payment.failed"); }
    @Bean NewTopic autoPaymentSuccess()       { return topic("autopayment.success"); }
}
