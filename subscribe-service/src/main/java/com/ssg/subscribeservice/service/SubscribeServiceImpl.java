package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.dto.SubscribeDto;
import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.dto.responseDto.PayInfluencerEmailNameResponseDto;
import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import com.ssg.subscribeservice.exception.NoAuthorizationHeader;
import com.ssg.subscribeservice.feign.FeignGetEmailNameByToken;
import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;
import com.ssg.subscribeservice.kafka.producer.SubscriptionProducer;
import com.ssg.subscribeservice.repository.SubscribeRepository;
import com.ssg.subscribeservice.repository.UserSubscribeRepository;
import com.ssg.subscribeservice.subsenum.SubscribeCode;
import com.ssg.subscribeservice.util.SecurityUtil;
import com.ssg.subscribeservice.util.SubscribeCodeConverter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscribeServiceImpl implements SubscribeService {

    private final SubscribeCodeConverter subscribeCodeConverter;
    private final SubscribeRepository subscribeRepository;
    private final UserSubscribeRepository userSubscribeRepository;
    private final SubscriptionProducer subscriptionProducer;
    private final FeignGetEmailNameByToken feignGetEmailNameByToken;
    private final HttpServletRequest request;

    //구독 요청
    public SubscriptionCreatedEvent createSubscription(String userId, String role){
        // 1. 롤값 코드로 변환
        SubscribeCode subscribeCode = subscribeCodeConverter.toSubscribeCode(role);
        log.info(role);

        // 2. 코드값으로 엔티티
        SubscribeEntity subscribeEntity = subscribeRepository.findBySubscribeCode(subscribeCode).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 구독코드입니다."));

        // 3. dto로 변환
        SubscribeDto subscribeDto = new ModelMapper().map(subscribeEntity, SubscribeDto.class);

        // 4. 현재 요청 헤더에서 Authorization 호출하여 토큰 가져옴 (feignClient 에서 담아서 보내야함)

        ResponseEntity<PayInfluencerEmailNameResponseDto> emailNameByToken = feignGetEmailNameByToken
                .getEmailNameByToken(
                        Optional.ofNullable(request.getHeader("Authorization"))
                                .orElseThrow(() ->
                                        new NoAuthorizationHeader("현재 요청의 Authorization 헤더에 jwt 토큰이 없습니다.")
                                ));

        // 5. feignClient 호출하여 이메일. 이름값 받아옴
        String userName = emailNameByToken.getBody().getUserName();
        String userEmail = emailNameByToken.getBody().getUserEmail();

        //이벤트 생성
        SubscriptionCreatedEvent subscriptionCreatedEvent = new SubscriptionCreatedEvent(userId,
                userName,
                userEmail,
                subscribeDto.getSubscribeAmount(),
                subscribeDto.getSubscribeId(),
                subscribeDto.getSubscribeName());

        //이벤트 발행
        subscriptionProducer.sendCreated("subscription.created", subscriptionCreatedEvent);

        return subscriptionCreatedEvent;
    }

    //카드 결제 여부
    public boolean isBillingRegistered(String userId) {
        //엔티티 꺼내서
        return userSubscribeRepository.findByUserId(userId)
                //카드 등록여부 꺼내고 없으면 false 반환
                .map(UserSubscribeEntity::getUserSubscribeBillingRegistered)
                .orElse(false);
    }
}
