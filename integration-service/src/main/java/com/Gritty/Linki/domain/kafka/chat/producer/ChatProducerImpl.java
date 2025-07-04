package com.Gritty.Linki.domain.kafka.chat.producer;

import com.Gritty.Linki.client.chatClient.service.ChatClientService;
import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.UserRepository;
import com.Gritty.Linki.domain.kafka.chat.Event.Event;
import com.Gritty.Linki.domain.kafka.chat.enums.EventType;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatProducerImpl implements ChatProducer{
    private final UserRepository userRepository;
    private final ProposalRepository proposalRepository;
    private final ChatClientService chatClientService;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    //프로듀서의 이벤트 발행
    @Override
    public void sendEvent(CustomUserDetails loginUser, EventType eventType,String proposalId){
        Event event = getEvent(loginUser,eventType,proposalId);
        try {
            //객체를 Json 으로 변환
            String message = mapper.writeValueAsString(event);
            kafkaTemplate.send("chat",message);
            log.info("메세지 발행 {}",message);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    //이벤트 객체 생성
    @Override
    public Event getEvent(CustomUserDetails loginUser, EventType eventType,String proposalId){
        User user = userRepository.findById(loginUser.getUserId()).orElseThrow(()-> new RuntimeException("존재하지 않는 회원"));

        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(()->new RuntimeException("존재하지 않는 제안서"));

        User partner = null;

        if(proposal.getInfluencer().getUserId().equals(loginUser.getUserId())) {
            partner  = proposal.getCampaign().getAdvertiser().getUser();
        }else {
            partner = proposal.getInfluencer().getUser();
        }

        return Event.builder()
                .eventType(eventType)
                .userName(user.getUserName())
                .userId(user.getUserId())           // 현재 사용자 ID 추가
                .userEmail(user.getUserEmail())
                .proposalId(proposalId)
                .partnerUserId(partner.getUserId())
                .partnerEmail(partner.getUserEmail())
                .build();
    }

}
