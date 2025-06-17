package com.ssg.chatservice.domain.kafka.listener;

import com.ssg.chatservice.domain.kafka.event.ChatCreatEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.util.Map;

@Slf4j
@SpringBootTest(properties = {
        // 테스트 환경에서 Kafka 서버 주소를 Embedded Kafka의 주소로 덮어쓰기
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "setting.ksb.topic=chat.create",               // application.yml 없이 직접 프로퍼티 주입
        "setting.ksb.group=test-consumer-group"
})
@EmbeddedKafka(partitions = 1, topics = "chat.create") // 테스트용 Kafka 브로커와 토픽 자동 생성
class ChatCreateListenerTest {
    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    @DisplayName("Kafka consumer - ChatCreateListener 수신 테스트")
    void chatCreateListenerTest()  throws InterruptedException  {
        /*카프카 프로듀서 설정
         *  Embedded Kafka 환경에서 사용할 프로듀서 속성 가져오기
         * */
        Map<String,Object> producerProps = KafkaTestUtils.producerProps(embeddedKafkaBroker);
        /* ProducerFactory  생성
         * 키는 문자열, 값은 DTO (ChatCreatDTO)
         * 직렬화 방식: StringSerializer, JsonSerializer
         *  */
        var producerFactory = new DefaultKafkaProducerFactory<String, ChatCreatEvent>(
                producerProps, new StringSerializer(), new JsonSerializer<>());
        // KafkaTemplate 생성 → 테스트 메시지 발행에 사용
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);

        /* 테스트 메세지 이벤트 생성
         * 토픽: chat.create
         * */
        ChatCreatEvent dto = new ChatCreatEvent("test100","test100","test100");
        ProducerRecord<String,ChatCreatEvent> record = new ProducerRecord<>("chat.create", dto);

        // 메시지 발행 → 실제로 Embedded Kafka에 write됨
        kafkaTemplate.send(record);
        // 리스너가 메시지를 수신할 수 있도록 대기 시간 확보(2초)
        Thread.sleep(2000);
        log.info("이벤트 발행 완료 ");



    }
}