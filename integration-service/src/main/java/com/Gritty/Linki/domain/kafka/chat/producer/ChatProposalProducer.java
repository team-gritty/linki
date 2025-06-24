package com.Gritty.Linki.domain.kafka.chat.producer;


import com.Gritty.Linki.domain.kafka.chat.Event.Event;

public interface ChatProposalProducer {
    void sendEvent(Event event);
}
