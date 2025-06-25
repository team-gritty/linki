package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import com.ssg.subscribeservice.kafka.event.BillingRegisteredEvent;
import com.ssg.subscribeservice.repository.UserSubscribeRepository;
import com.ssg.subscribeservice.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BillingRegisteredHandlerImpl implements BillingRegisteredHandler {

    private final UserSubscribeRepository userSubscribeRepository;
    private final IdGenerator idGenerator;

    @Override
    public void handle(BillingRegisteredEvent event) {
        //이벤트에서 유저 아이디 가져옴
        String userId = event.userId();
        log.info("✅ [billing.registered] handling for userId={}", userId);

        //존재하면
        userSubscribeRepository.findById(userId).ifPresentOrElse(
                //업데이트
                entity -> {
                    entity.setUserSubscribeBillingRegistered(true);
                    userSubscribeRepository.save(entity);
                },
                //존재하지 않으면 insert
                () -> {
                    userSubscribeRepository.save(UserSubscribeEntity.builder()
                            .userSubscriptionId(idGenerator.userSubscribeId())
                            .userId(userId)
                            .userSubscribeBillingRegistered(true)
                            .build());
                }
        );
    }
}
