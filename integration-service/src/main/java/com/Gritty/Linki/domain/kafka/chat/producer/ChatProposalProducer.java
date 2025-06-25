package com.Gritty.Linki.domain.kafka.chat.producer;


import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.kafka.chat.Event.Event;
import com.Gritty.Linki.domain.kafka.chat.enums.EventType;

public interface ChatProposalProducer {
    void sendEvent(CustomUserDetails loginUser, EventType eventType, String proposalId);
    Event getEvent(CustomUserDetails loginUser, EventType eventType,String proposalId);
}
