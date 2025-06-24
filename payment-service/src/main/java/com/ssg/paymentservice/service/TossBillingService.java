package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.common.toss.TossConfig;
import com.ssg.paymentservice.common.util.IdGenerator;
import com.ssg.paymentservice.common.util.TossBasicAuthHeaderUtil;
import com.ssg.paymentservice.dto.requestdto.AuthCardRequestDto;
import com.ssg.paymentservice.dto.requestdto.BillingKeyResponseDto;
import com.ssg.paymentservice.exception.BillingNotFoundException;
import com.ssg.paymentservice.feignClient.FeignGetBillingKey;
import com.ssg.paymentservice.repository.BillingRepository;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TossBillingService implements BillingService {

    private final TossBasicAuthHeaderUtil tossBasicAuthHeaderUtil;
    private final TossConfig tossConfig;
    private final FeignGetBillingKey feignGetBillingKey;
    private final IdGenerator idGenerator;
    private final BillingRepository billingRepository;

    //인증키 , userId(customerKey)
    @Override
    public String confirmBilling(String authKey, String customerKey) {

        //Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

        AuthCardRequestDto authCardRequestDto = AuthCardRequestDto.builder()
                .customerKey(customerKey)
                .authKey(authKey)
                .build();

        BillingKeyResponseDto res = feignGetBillingKey
                .getBillingKey(authorizationHeader, authCardRequestDto)
                .getBody();

        BillingEntity billing = BillingEntity.builder()
                .billingId(idGenerator.billingId())
                .billingKey(res.getBillingKey())
                .userId(res.getCustomerKey())
                .billingCreateAt(res.getAuthenticatedAt().toLocalDateTime())
                .billingStatus(true)
                .paymentApproveStatus(true)
                .cardCompany(res.getCardCompany())
                .cardNumber(res.getCardNumber())
                .cardType(res.getCard().getCardType())
                .cardOwnerType(res.getCard().getOwnerType())
                .issuerCode(res.getCard().getIssuerCode())
                .acquirerCode(res.getCard().getAcquirerCode())
                .build();

        //billing 튜플 저장
        billingRepository.save(billing);

        return res.getBillingKey();
    }

    public String getBillingKeyByUserId(String userId) {
        //billingEntity 에서 billingKey 얻어옴
        String billingKey = billingRepository.findByUserId(userId)
                .orElseThrow(() -> new BillingNotFoundException("해당 유저아이디의 빌링키를 확인할 수 없습니다."))
                .getBillingKey();






        return null;
    }
}
