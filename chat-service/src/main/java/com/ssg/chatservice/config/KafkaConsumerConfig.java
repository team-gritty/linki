package com.ssg.chatservice.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@EnableKafka // Kafka 리스너(@KafkaListener)를 사용하기 위한 설정
@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {
     // Kafka 서버 주소/Consumer 그룹 ID/Key,Value/역직렬화 방식 등 yml 설정
    private final KafkaProperties kafkaProperties;
    /**
     * Kafka Consumer 설정을 담은 Factory Bean 선언
     * - yml 설정이 들어있는 KafkaProperties를 기반으로 ConsumerFactory 생성 */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties());
    }

    /**
     * Kafka 리스너 컨테이너 설정
     * - @KafkaListener가 메시지를 처리할 수 있도록 Listener 컨테이너를 생성
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        // ConsumerFactory 주입
        factory.setConsumerFactory(consumerFactory());
        // 수동 커밋 모드 설정 → 메시지를 명시적으로 처리한 후에만 커밋
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }
}
