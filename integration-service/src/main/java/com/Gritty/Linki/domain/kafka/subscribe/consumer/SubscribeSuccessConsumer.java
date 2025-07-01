package com.Gritty.Linki.domain.kafka.subscribe.consumer;

import com.Gritty.Linki.domain.kafka.subscribe.consumehandler.SubscribeSuccessConsumeHandler;
import com.Gritty.Linki.domain.kafka.subscribe.event.SubscribeSuccessEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeSuccessConsumer {

    private final ObjectMapper objectMapper;
    private final SubscribeSuccessConsumeHandler subscribeSuccessConsumeHandler;

    @KafkaListener(topics = "subscribe.success", groupId = "user-group")
    public void consumeSubscribeSuccess(String message) {
        log.info("Consuming subscribe success event: {}", message);
        try {
            SubscribeSuccessEvent subscribeSuccessEvent = objectMapper.readValue(message, SubscribeSuccessEvent.class);
            //구독 처리
            subscribeSuccessConsumeHandler.subscribeSuccess(subscribeSuccessEvent.userId());
        } catch (JsonProcessingException e){
             log.error(e.getMessage());
        } catch (Exception e){
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
