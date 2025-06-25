package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.common.kafka.event.BillingRegisteredEvent;
import com.ssg.paymentservice.common.kafka.event.SubscriptionCreatedEvent;
import com.ssg.paymentservice.common.kafka.producer.BillingEventProducer;
import com.ssg.paymentservice.common.toss.TossConfig;
import com.ssg.paymentservice.common.util.IdGenerator;
import com.ssg.paymentservice.common.util.TossBasicAuthHeaderUtil;
import com.ssg.paymentservice.dto.requestdto.AuthCardRequestDto;
import com.ssg.paymentservice.dto.responsedto.BillingKeyResponseDto;
import com.ssg.paymentservice.dto.responsedto.PayInfluencerEmailNameResponseDto;
import com.ssg.paymentservice.exception.BillingNotFoundException;
import com.ssg.paymentservice.exception.NoAuthorizationHeader;
import com.ssg.paymentservice.feignClient.FeignGetBillingKey;
import com.ssg.paymentservice.feignClient.FeignGetEmailNameByToken;
import com.ssg.paymentservice.repository.BillingRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TossBillingService implements BillingService {

    private final TossBasicAuthHeaderUtil tossBasicAuthHeaderUtil;
    private final TossConfig tossConfig;
    private final FeignGetBillingKey feignGetBillingKey;
    private final IdGenerator idGenerator;
    private final BillingRepository billingRepository;
    private final HttpServletRequest request;
    private final FeignGetEmailNameByToken feignGetEmailNameByToken;
    private final BillingEventProducer billingEventProducer;


    //인증키 , userId(customerKey)
    @Override
    public BillingKeyResponseDto confirmBilling(String authKey, String customerKey) {

        //Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

        //빌링키 발급 요청 dto 작성
        AuthCardRequestDto authCardRequestDto = AuthCardRequestDto.builder()
                .customerKey(customerKey)
                .authKey(authKey)
                .build();

        //빌링키 응답 dto
        BillingKeyResponseDto billingResponse = feignGetBillingKey.getBillingKey(authorizationHeader, authCardRequestDto).getBody();

        //응답객체 엔티티 저장
        BillingEntity billingEntity = BillingEntity.builder()
                .billingId(idGenerator.billingId())
                .billingKey(billingResponse.getBillingKey())
                .userId(billingResponse.getCustomerKey())
                .active(true)// 빌링키 발급 직후이므로 활성
                .autoBillingActivated(false)// 첫 결제 전이므로 비활성
                .failCount(0)
                .cardCompany(billingResponse.getCardCompany())
                .cardNumber(billingResponse.getCardNumber())
                .cardType(billingResponse.getCard().getCardType())
                .cardOwnerType(billingResponse.getCard().getOwnerType())
                .issuerCode(billingResponse.getCard().getIssuerCode())
                .acquirerCode(billingResponse.getCard().getAcquirerCode())
                .build();


        //이벤트 발행
        billingEventProducer.billingRegistered("billing.registered", new BillingRegisteredEvent(customerKey));
        //저장
        billingRepository.save(billingEntity);
        return billingResponse;
    }
//
//    public void firstPayment(SubscriptionCreatedEvent evt) {
//
//        // 1️⃣ 사용자 BillingKey 조회 (없으면 예외)
//        BillingEntity bill = billingRepository.findByUserId(evt.userId())
//                .orElseThrow(() -> new IllegalStateException("BillingKey 없음"));
//
//        // 2️⃣ 구독 금액 계산 (플랜 금액)
//        int amount = plan.amount(evt.subscribeCode());
//
//        // 3️⃣ Toss 결제 API 호출 → 결과 수신
//        PaymentResult r = toss.pay(bill.getBillingKey(), amount);   // 실제 구현은 TossPayRequest 등
//
//        // 4️⃣ 결제 이력 저장 (성공/실패 공통)
//        histRepo.save(PaymentHistoryEntity.builder()
//                .paymentHistoryId(UUID.randomUUID().toString())         // PK UUID
//                .billingId(bill.getBillingId())
//                .userId(evt.userId())
//                .subscribeCode(evt.subscribeCode())
//                .amount(amount)
//                .paidAt(LocalDateTime.now())
//                .success(r.isSuccess())
//                .transactionKey(r.txKey())
//                .failCode(r.failCode())
//                .failMessage(r.failMsg())
//                .build());
//
//        /* ───────── 성공 처리 ───────── */
//        if (r.isSuccess()) {
//            bill.setLastPaidAt(LocalDateTime.now());                // 마지막 결제 시각
//            bill.setNextBillingAt(                                   // 다음 청구일 = +주기
//                    bill.getLastPaidAt().plusMonths(plan.period(evt.subscribeCode())));
//            bill.setAutoBillingActivated(true);                     // 자동결제 ON
//            bill.setFailCount(0);                                   // 실패 카운트 리셋
//
//            // Kafka → subscription-service: 결제 성공 이벤트
//            producer.paymentSuccess(
//                    new PaymentSuccessEvent(evt.userId(),
//                            evt.subscribeCode(),
//                            bill.getNextBillingAt()));
//
//            /* ───────── 실패 처리 ───────── */
//        } else {
//            bill.setFailCount(bill.getFailCount() + 1);             // 실패 누적 +1
//
//            // Kafka → subscription-service: 결제 실패 이벤트
//            producer.paymentFailed(
//                    new PaymentFailedEvent(evt.userId(), r.failMsg()));
//        }
//    }














    public String requestPaymentWithBilling(String userId) {
        //billingEntity 에서 billingKey 얻어옴
        String billingKey = billingRepository.findByUserId(userId)
                .orElseThrow(() -> new BillingNotFoundException("해당 유저아이디의 빌링키를 확인할 수 없습니다."))
                .getBillingKey();

        //현재 요청 헤더에서 Authorization 호출
        Optional<String> token = Optional.ofNullable(request.getHeader("Authorization"));
        String jwtToken = token.orElseThrow(() ->
                new NoAuthorizationHeader("현재 요청의 Authorization 헤더에 jwt 토큰이 없습니다.")
        );

        //feignClient 호출하여 이메일. 이름값 받아옴
        ResponseEntity<PayInfluencerEmailNameResponseDto> emailNameByToken = feignGetEmailNameByToken.getEmailNameByToken(jwtToken);



        //Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

//        //결제요청 body json dto 생성
//        AutoPaymentRequestDto.builder()
//                .customerKey(userId)
//                .amount()
//                .build();


        return null;
    }
}
