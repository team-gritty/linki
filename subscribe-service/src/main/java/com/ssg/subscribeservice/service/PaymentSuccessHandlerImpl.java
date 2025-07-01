package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import com.ssg.subscribeservice.kafka.event.PaymentSuccessEvent;
import com.ssg.subscribeservice.kafka.event.SubscribeSuccessEvent;
import com.ssg.subscribeservice.kafka.producer.SubscribeSuccessProducer;
import com.ssg.subscribeservice.repository.SubscribeRepository;
import com.ssg.subscribeservice.repository.UserSubscribeRepository;
import com.ssg.subscribeservice.subsenum.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class PaymentSuccessHandlerImpl implements PaymentSuccessHandler {

    private final UserSubscribeRepository userSubscribeRepository;
    private final SubscribeRepository subscribeRepository;
    private final ModelMapper modelMapper;
    private final SubscribeSuccessProducer subscribeSuccessProducer;

    public UserSubscribeDto handle(PaymentSuccessEvent paymentSuccessEvent) {
        // 1-1 유저 구독 정보 조회 (없으면 예외)
        UserSubscribeEntity entity = userSubscribeRepository.findByUserId(paymentSuccessEvent.userId())
                .orElseThrow(() -> new IllegalArgumentException("해당 userId의 구독 정보가 존재하지 않습니다."));

        // 1-2 구독 엔티티 얻어오기
        SubscribeEntity subscribeEntity = subscribeRepository.findById(paymentSuccessEvent.subscribeId()).orElseThrow(() ->
                new NoSuchElementException("해당 SubscribeId에 해당하는 Subscription 이 없습니다."));

        // 2. 정보 갱신
        updateUSEntityWhenPaymentSuccess(paymentSuccessEvent, entity, subscribeEntity);

        // 3. 저장
        userSubscribeRepository.save(entity);

        //유저 구독상태 변경 요청 이벤트 발행
        SubscribeSuccessEvent subscribeSuccessEvent = new SubscribeSuccessEvent(paymentSuccessEvent.userId());
        subscribeSuccessProducer.sendSuccess("subscribe.success",subscribeSuccessEvent);

        // 4. DTO 변환 및 반환
        return modelMapper.map(entity, UserSubscribeDto.class);
    }

    private static void updateUSEntityWhenPaymentSuccess(PaymentSuccessEvent paymentSuccessEvent, UserSubscribeEntity entity, SubscribeEntity subscribeEntity) {
        entity.setUserSubscribeStartedAt(
                entity.getUserSubscribeStartedAt() != null
                        ? entity.getUserSubscribeStartedAt()
                        : paymentSuccessEvent.nextBillingAt().minusMonths(1) // 시작일이 없다면 이전 달로 추정
        );
        entity.setSubscribe(subscribeEntity);
        entity.setUserSubscribeNextBillingAt(paymentSuccessEvent.nextBillingAt());
        entity.setUserSubscribeStatus(SubscriptionStatus.ACTIVE);
    }
}