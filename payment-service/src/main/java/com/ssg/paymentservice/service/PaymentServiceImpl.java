package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.dto.BillingInfoDto;
import com.ssg.paymentservice.repository.BillingRepository;
import com.ssg.paymentservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BillingRepository billingRepository;
    private final PaymentHistoryService paymentHistoryService;
    private final ModelMapper modelMapper;


    @Override
    @Transactional
    public BillingDto getBillingDtoByUserId(String userId) {
        // 유저 아이디로 확인
        BillingEntity billingEntity = billingRepository.findByUserId(userId).orElseThrow(() ->
                new NoSuchElementException("해당 유저아이디의 빌링 튜플이 없습니다."));

        //dto 맵핑해서 반환
        return new ModelMapper().map(billingEntity, BillingDto.class);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public List<BillingDto> ListAutoBillingAtNow() {
        List<BillingEntity> billingsToProcess =
                billingRepository.findByAutoBillingActivatedIsTrueAndNextBillingAtBefore(LocalDateTime.now());

        return billingsToProcess.stream().map(billingEntity -> {
            return modelMapper.map(billingEntity, BillingDto.class);
        }).toList();


//        for (BillingEntity billing : billingsToProcess) {
//            try {
//                // 1. 실제 결제 요청 호출 (ex: Toss API 호출)
//                // boolean success = paymentProvider.charge(billing); 등
//
//                // [가정] 결제 성공 처리
//                billing.setLastPaidAt(LocalDateTime.now());
//                billing.setNextBillingAt(LocalDateTime.now().plusMonths(1));
//                billing.setFailCount(0);
//
//            } catch (Exception e) {
//                // 실패한 경우
//                billing.setFailCount((billing.getFailCount() == null ? 1 : billing.getFailCount() + 1));
//                // 필요하다면 billing.setActive(false); 등 처리 가능
//            }
//        }
    }



}
