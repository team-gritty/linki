package com.ssg.chatservice.domain.kafka.listener;

import com.ssg.chatservice.client.PartnerApiClient;
import com.ssg.chatservice.client.PartnerInfoResponse;
import com.ssg.chatservice.domain.chat.repository.ChatRepository;
import com.ssg.chatservice.entity.Chat;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

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
    @Autowired
    private ChatCreateListener listener;

    @Autowired
    private ChatRepository chatRepository;

    @MockBean
    private PartnerApiClient partnerApiClient;

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
        var producerFactory = new DefaultKafkaProducerFactory<String, String>(
                producerProps, new StringSerializer(), new StringSerializer());
        // KafkaTemplate 생성 → 테스트 메시지 발행에 사용
        var kafkaTemplate = new KafkaTemplate<>(producerFactory);

        /* 테스트 메세지 이벤트 생성
         * 토픽: chat.create
         * */
        String Event = """ 
        {
            "proposalId": "TEST999"
        } 
        """;
        ProducerRecord<String,String> record = new ProducerRecord<>("chat.create", Event);

        // 메시지 발행 → 실제로 Embedded Kafka에 write됨
        kafkaTemplate.send(record);
        // 리스너가 메시지를 수신할 수 있도록 대기 시간 확보(2초)
        Thread.sleep(2000);
        log.info("이벤트 발행 완료 ");
    }


    @Test
    @DisplayName("onMessage() - 채팅방 생성 성공")
    void chatListenerProcessTest() throws Exception {
        // given - 파트너 응답 mock
        given(partnerApiClient.getPartnerInfo("TEST999"))
                .willReturn(new PartnerInfoResponse("partnerId", "partnerName", "TEST999", "profile.png", "채널명", "PENDING"));

        // ConsumerRecord 생성
        ConsumerRecord<String, String> record = new ConsumerRecord<>(
                "chat.create", 0, 0, null,
                """
                {
                    "proposalId": "TEST999"
                }
                """
        );

        // acknowledgment 목 생성
        Acknowledgment ack = () -> System.out.println("acknowledged ✅");

        // when - 리스너 직접 호출
        listener.onMessage(record, ack);

        // then - DB에 채팅방이 잘 저장됐는지 확인
        Chat saved = chatRepository.findByProposalId("TEST999");
        assertThat(saved).isNotNull();
        assertThat(saved.getProposalId()).isEqualTo("TEST999");
    }
}
