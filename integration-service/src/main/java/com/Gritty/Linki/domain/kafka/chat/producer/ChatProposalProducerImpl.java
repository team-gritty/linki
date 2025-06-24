package com.Gritty.Linki.domain.kafka.chat.producer;

import com.Gritty.Linki.domain.kafka.chat.Event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProposalProducerImpl implements ChatProposalProducer{
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    @Override
    public void sendEvent(Event event){
        try {
            //객체를 Json 으로 변환
            String message = mapper.writeValueAsString(event);
            kafkaTemplate.send("chat",message);
            log.info("메세지 발행 {}",message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
