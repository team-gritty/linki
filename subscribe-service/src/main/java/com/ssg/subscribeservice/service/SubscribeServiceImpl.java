package com.ssg.subscribeservice.service;

import com.ssg.subscribeservice.dto.SubscribeDto;
import com.ssg.subscribeservice.dto.UserSubscribeDto;
import com.ssg.subscribeservice.entity.SubscribeEntity;
import com.ssg.subscribeservice.entity.UserSubscribeEntity;
import com.ssg.subscribeservice.kafka.event.SubscriptionCreatedEvent;
import com.ssg.subscribeservice.repository.SubscribeRepository;
import com.ssg.subscribeservice.repository.UserSubscribeRepository;
import com.ssg.subscribeservice.subsenum.SubscribeCode;
import com.ssg.subscribeservice.util.SecurityUtil;
import com.ssg.subscribeservice.util.SubscribeCodeConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscribeServiceImpl implements SubscribeService {

    private final SecurityUtil securityUtil;
    private final SubscribeCodeConverter subscribeCodeConverter;
    private final SubscribeRepository subscribeRepository;
    private final UserSubscribeRepository userSubscribeRepository;

    //구독 요청
    public void createSubscription(){
        //홀더에서 롤 값 가져옴
        String role = securityUtil.getCurrentUserRole();
        //롤값 코드로 변환
        SubscribeCode subscribeCode = subscribeCodeConverter.toSubscribeCode(role);
        //코드값으로 엔티티
        SubscribeEntity subscribeEntity = subscribeRepository.findBySubscribeCode(subscribeCode).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 구독코드입니다."));
        //dto로 변환
        SubscribeDto subscribeDto = new ModelMapper().map(subscribeEntity, SubscribeDto.class);
        //이벤트 생성
        SubscriptionCreatedEvent subscriptionCreatedEvent = new SubscriptionCreatedEvent(securityUtil.getCurrentUserId(),
                subscribeDto.getSubscribeAmount(),
                subscribeDto.getSubscribeId(),
                subscribeDto.getSubscribeName());
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
