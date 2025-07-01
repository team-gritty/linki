package com.Gritty.Linki.domain.kafka.subscribe.consumehandler;

import com.Gritty.Linki.config.security.UserRepository;
import com.Gritty.Linki.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeSuccessConsumeHandlerImpl implements SubscribeSuccessConsumeHandler {
    private final UserRepository userRepository;

    public User subscribeSuccess(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("User with id " + userId + " not found")
        );

        if(user.getUserPayStatus() == 0) {
            user.setUserPayStatus(1);
            userRepository.save(user);
        }
        log.info("zzzzzzzzzzzzzzzzzzzz");
//        throw new IllegalStateException("Forced failure for DLQ testing"); //dlq 테스트용
        return user;

    }
}
