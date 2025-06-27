package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.dto.BillingInfoDto;
import com.ssg.paymentservice.repository.BillingRepository;
import com.ssg.paymentservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BillingRepository billingRepository;
    private final PaymentHistoryService paymentHistoryService;


    @Override
    public BillingDto getBillingDtoByUserId(String userId) {
        // 유저 아이디로 확인
        BillingEntity billingEntity = billingRepository.findByUserId(userId).orElseThrow(() ->
                new NoSuchElementException("해당 유저아이디의 빌링 튜플이 없습니다."));

        //dto 맵핑해서 반환
        return new ModelMapper().map(billingEntity, BillingDto.class);
    }

    @Override
    public BillingInfoDto getBillingInfoDto(String userId) {

        // 1. 빌링객체 가져옴
        BillingDto billingDtoByUserId = getBillingDtoByUserId(userId);

        // 2. 구독 개월수 가져옴
        Integer monthCount = paymentHistoryService.countByUserIdAndSuccessTrue(userId);

        // 3. BillingInfoDto 만들고 반환
        return BillingInfoDto.builder()
                .nextBillingAt(billingDtoByUserId.getNextBillingAt())
                .cardCompany(billingDtoByUserId.getCardCompany())
                .cardNumber(billingDtoByUserId.getCardNumber())
                .monthCount(monthCount)
                .build();
    }



}
