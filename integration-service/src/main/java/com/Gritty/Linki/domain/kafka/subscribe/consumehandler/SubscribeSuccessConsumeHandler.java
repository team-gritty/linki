package com.Gritty.Linki.domain.kafka.subscribe.consumehandler;

import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


public interface SubscribeSuccessConsumeHandler {
    User subscribeSuccess(String userId);
}
