package com.ssg.chatservice.domain.kafka.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssg.chatservice.domain.chat.enums.ErrorCode;
import com.ssg.chatservice.domain.chat.service.ChatService;
import com.ssg.chatservice.domain.kafka.Event.Event;
import com.ssg.chatservice.domain.notification.dto.NotificationDto;
import com.ssg.chatservice.domain.notification.service.NotificationService;
import com.ssg.chatservice.exception.ChatException;
import com.ssg.chatservice.domain.mail.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka 리스너 클래스 - 채팅방 생성 요청을 처리하는 Consumer
 *
 * - 수신 대상: Kafka 토픽 (chat.create)
 * - 처리 데이터: ChatCreatDTO (JSON → 객체 자동 변환)
 * - 오프셋 커밋 방식: 수동 커밋 (MANUAL_IMMEDIATE)
 *
 * 수동 커밋을 사용함으로써, 메시지를 정확히 처리한 후에만 커밋
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatListener {

    private final ChatService chatService;
    private final MailService mailService;
    private final NotificationService notificationService;

    private final ObjectMapper mapper;

    /**
     * Kafka에서 chat.create 토픽 메시지를 수신하여 처리하는 메서드
     *
     * @param consumerRecord Kafka에서 받은 전체 메시지 객체 (Key, Value, Offset 등 포함)
     * @param acknowledgment 수동 커밋을 수행하기 위한 객체
     * @KafkaListener - topics: 수신할 Kafka 토픽 (설정 파일에서 주입받음)
     * - groupId: 컨슈머 그룹 식별자. 같은 그룹 내에서는 메시지를 분산 처리함
     * - containerFactory: 수동 커밋 전략이 적용된 KafkaListenerContainerFactory 빈 이름
     */
    @KafkaListener(
            topics = "${kafka.setting.topic}",            // application.yml에 정의된 토픽명
            groupId = "${kafka.setting.group}",        // Kafka consumer group
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void receiveEvent(ConsumerRecord<String, String> consumerRecord,
                             Acknowledgment acknowledgment) {
        try {
            //수신 받은 이벤트를 객체로 변경
            Event event = mapper.readValue(consumerRecord.value(), Event.class);
            log.info("메세지 수신 {}", consumerRecord.value());
            //이벤트 타입 핸들러호출
            event.getEventType().handle(chatService, event.getProposalId());
            chatService.updateNegoStatus(event.getProposalId(), event.getEventType());

            NotificationDto notificationDto = NotificationDto.builder()
                    .userName(event.getUserName())
                    .eventType(event.getEventType())
                    .build();
            //채팅방 계약상태 변경

            // 수동 커밋 수행 → 이 시점 이후에만 메시지가 "정상 소비됨"으로 간주됨
            // acknowledgment.acknowledge()를 호출하지 않으면 메시지는 다시 재시도됨
            acknowledgment.acknowledge();

            //메일 발송
            mailService.sendPostNotification(event.getUserEmail(), notificationDto.getMessage());
            notificationService.sendNotificationToChat(event.getProposalId(), notificationDto.getMessage());

        } catch (JsonProcessingException e) {
            log.error("Kafka 메시지 역직렬화 실패", e);
            throw new ChatException(ErrorCode.KAFKA_DESERIALIZATION_ERROR);
        } catch (Exception e) {
            log.error("Kafka 이벤트 처리 중 예외 발생", e);
            throw new ChatException(ErrorCode.KAFKA_UNHANDLED_ERROR);
        }
    }
}
