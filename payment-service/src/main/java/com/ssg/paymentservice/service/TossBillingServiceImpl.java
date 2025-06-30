package com.ssg.paymentservice.service;

import com.ssg.paymentservice.common.entity.BillingEntity;
import com.ssg.paymentservice.common.entity.PaymentHistoryEntity;
import com.ssg.paymentservice.common.kafka.event.AutoPaymentSuccessEvent;
import com.ssg.paymentservice.common.kafka.event.BillingRegisteredEvent;
import com.ssg.paymentservice.common.kafka.event.PaymentSuccessEvent;
import com.ssg.paymentservice.common.kafka.event.SubscriptionCreatedEvent;
import com.ssg.paymentservice.common.kafka.producer.AutoPaymentSuccessProducer;
import com.ssg.paymentservice.common.kafka.producer.BillingEventProducer;
import com.ssg.paymentservice.common.kafka.producer.PaymentSuccessProducer;
import com.ssg.paymentservice.common.toss.TossConfig;
import com.ssg.paymentservice.common.util.IdGenerator;
import com.ssg.paymentservice.common.util.TossBasicAuthHeaderUtil;
import com.ssg.paymentservice.dto.dto.BillingDto;
import com.ssg.paymentservice.dto.requestdto.AuthCardRequestDto;
import com.ssg.paymentservice.dto.requestdto.AutoPaymentRequestDto;
import com.ssg.paymentservice.dto.responsedto.AutoPaymentResponseDto;
import com.ssg.paymentservice.dto.responsedto.BillingKeyResponseDto;
import com.ssg.paymentservice.feignClient.FeignGetBillingKey;
import com.ssg.paymentservice.feignClient.FeignRequestTossPayment;
import com.ssg.paymentservice.repository.BillingRepository;
import com.ssg.paymentservice.repository.PaymentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TossBillingServiceImpl implements TossBillingService {

    private final TossBasicAuthHeaderUtil tossBasicAuthHeaderUtil;
    private final TossConfig tossConfig;
    private final FeignGetBillingKey feignGetBillingKey;
    private final IdGenerator idGenerator;
    private final BillingRepository billingRepository;
    private final BillingEventProducer billingEventProducer;
    private final FeignRequestTossPayment feignRequestTossPayment;
    private final PaymentSuccessProducer paymentSuccessProducer;
    private final PaymentService paymentService;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final AutoPaymentSuccessProducer autoPaymentSuccessProducer;


    //인증키 , userId(customerKey)
    @Override
    @Transactional
    public BillingKeyResponseDto confirmBilling(String authKey, String customerKey) {

        // 1. Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

        // 2. 빌링키 발급 요청 dto 작성
        AuthCardRequestDto authCardRequestDto = AuthCardRequestDto.builder()
                .customerKey(customerKey)
                .authKey(authKey)
                .build();

        // 3. 빌링키 응답 dto
        BillingKeyResponseDto billingResponse = feignGetBillingKey.getBillingKey(authorizationHeader, authCardRequestDto).getBody();

        // 4. 기존 BillingEntity 여부 확인 후 업데이트 또는 신규 저장
        Optional<BillingEntity> existing = billingRepository.findByUserId(billingResponse.getCustomerKey());
        BillingEntity billingEntity;
        // 빌링키 이미 존재하면 업데이트
        if (existing.isPresent()) {
            changeBillingKey(existing, billingResponse);
        } else {
            //없으면 생성
            billingEntity = saveBillingResponse(billingResponse);
            billingRepository.save(billingEntity);

            // 6. 이벤트 발행
            billingEventProducer.billingRegistered("billing.registered", new BillingRegisteredEvent(customerKey));
        }

        return billingResponse;
    }

    private static void changeBillingKey(Optional<BillingEntity> existing, BillingKeyResponseDto billingResponse) {
        BillingEntity billingEntity;
        billingEntity = existing.get();
        billingEntity.setBillingKey(billingResponse.getBillingKey());
        billingEntity.setCardCompany(billingResponse.getCardCompany());
        billingEntity.setCardNumber(billingResponse.getCardNumber());
        billingEntity.setCardType(billingResponse.getCard().getCardType());
        billingEntity.setCardOwnerType(billingResponse.getCard().getOwnerType());
        billingEntity.setIssuerCode(billingResponse.getCard().getIssuerCode());
        billingEntity.setAcquirerCode(billingResponse.getCard().getAcquirerCode());
        billingEntity.setActive(true);
    }

    @Override
    @Transactional
    public void firstPayment(String userId, SubscriptionCreatedEvent subscriptionCreatedEvent) {
        // 1. 유저 아이디로 확인
        BillingDto billingDto = paymentService.getBillingDtoByUserId(userId);

        //  2. 사용자 BillingKey 조회 (없으면 예외)
        String billingKey = billingDto.getBillingKey();

        // 3. Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

        // 4. orderId 생성
        String orderId = idGenerator.OrderId();

        // 5. 결제요청 body
        AutoPaymentRequestDto autoPaymentRequestDto = getAutoPaymentRequestDto(userId, subscriptionCreatedEvent, orderId);

        // 6. FeignClient 를 통해 결제 요청
        ResponseEntity<AutoPaymentResponseDto> response = feignRequestTossPayment.requestPayment(
                billingKey,
                authorizationHeader,
                autoPaymentRequestDto
        );

        // 7. 응답 본문 저장
        AutoPaymentResponseDto autoPaymentResponseDto = response.getBody();
        log.info(autoPaymentResponseDto.toString());

        // 8. 결제 성공 처리
        if (response.getStatusCode().is2xxSuccessful()
                && autoPaymentResponseDto != null
                && "DONE".equalsIgnoreCase(autoPaymentResponseDto.getStatus())
                && autoPaymentResponseDto.getApprovedAt() != null) {
            // 결제 성공 처리
            doPaymentSuccess(userId, subscriptionCreatedEvent, billingDto, autoPaymentResponseDto);

            // history dto 생성 (payment 키 있음)
            PaymentHistoryEntity paymentHistoryEntity = createPaymentHistoryDto(userId, subscriptionCreatedEvent, orderId, autoPaymentResponseDto);

            // db 저장
            paymentHistoryRepository.save(paymentHistoryEntity);

        } else {

            //결제 실패 처리
            doPaymentFail(billingDto, autoPaymentResponseDto);
        }
    }

    @Override
    @Transactional
    public List<BillingDto> processAutoBilling(List<BillingDto> billingDtoList) {
        //Basic 인증 헤더
        String authorizationHeader = tossBasicAuthHeaderUtil.createBasicAuth(tossConfig.getSecretKey());

        // 결제 결과를 저장할 리스트
        List<BillingDto> processedBillings = new java.util.ArrayList<>();

        billingDtoList.forEach(billingDto -> {
            // 결제 요청 DTO 생성
            AutoPaymentRequestDto autoPaymentRequestDto = AutoPaymentRequestDto.builder()
                    .customerKey(billingDto.getUserId())
                    .amount(billingDto.getAmount())
                    .orderId(idGenerator.OrderId())
                    .orderName(billingDto.getOrderName())
                    .customerEmail(billingDto.getCustomerEmail())
                    .customerName(billingDto.getCustomerName())
                    .taxFreeAmount(0)
                    .build();

            try {
                //토스 결제 요청 with billingKey
                ResponseEntity<AutoPaymentResponseDto> response = feignRequestTossPayment.requestPayment(
                        billingDto.getBillingKey(),
                        authorizationHeader,
                        autoPaymentRequestDto
                );

                //응답값 저장
                AutoPaymentResponseDto responseBody = response.getBody();

                //성공처리
                if (response.getStatusCode().is2xxSuccessful()
                        && responseBody != null
                        && "DONE".equalsIgnoreCase(responseBody.getStatus())
                        && responseBody.getApprovedAt() != null) {
                    //다음 결제일 업데이트
                    billingDto.setNextBillingAt(responseBody.getApprovedAt().toLocalDateTime().plusMonths(1));

                    BillingEntity billingEntity = new ModelMapper().map(billingDto, BillingEntity.class);

                    billingRepository.save(billingEntity);
                    processedBillings.add(billingDto);

                    //성공 이벤트 생성
                    AutoPaymentSuccessEvent autoPaymentSuccessEvent = new AutoPaymentSuccessEvent(billingDto.getUserId(), billingDto.getNextBillingAt());
                    log.info(autoPaymentSuccessEvent.toString());

                    // Kafka 이벤트 발행 (예: 결제 성공 이벤트) to subscribe service
                    autoPaymentSuccessProducer.autoSendPaymentSuccessEvent("autoPayment.success", autoPaymentSuccessEvent);

                    //history 저장
                    PaymentHistoryEntity paymentHistoryEntity = PaymentHistoryEntity.builder()
                            .paymentHistoryId(idGenerator.OrderId())
                            .userId(billingDto.getUserId())
                            .orderId(autoPaymentRequestDto.getOrderId())
                            .amount(autoPaymentRequestDto.getAmount())
                            .paidAt(responseBody.getApprovedAt().toLocalDateTime().plusMonths(1))
                            .success(true)
                            .paymentKey(responseBody.getPaymentKey())
                            .build();
                    paymentHistoryRepository.save(paymentHistoryEntity);

                } else { //실패처리
                    updateFailCountWhenTossPayRequest(billingDto);
                    billingRepository.save(new org.modelmapper.ModelMapper().map(billingDto, BillingEntity.class));
                }
            } catch (Exception e) {
                updateFailCountWhenTossPayRequest(billingDto);
                billingRepository.save(new org.modelmapper.ModelMapper().map(billingDto, BillingEntity.class));
                log.error("자동결제 요청 중 오류 발생: userId={}, message={}", billingDto.getUserId(), e.getMessage());
            }
        });

        return processedBillings;
    }

    private PaymentHistoryEntity createPaymentHistoryDto(String userId, SubscriptionCreatedEvent subscriptionCreatedEvent, String orderId, AutoPaymentResponseDto autoPaymentResponseDto) {
        return PaymentHistoryEntity.builder()
                .paymentHistoryId(idGenerator.paymentHistoryId())
                .userId(userId)
                .orderId(orderId)
                .subscribeId(subscriptionCreatedEvent.subscribeId())
                .amount(subscriptionCreatedEvent.subscribeAmount())
                .paidAt(autoPaymentResponseDto.getApprovedAt().toLocalDateTime())
                .success(true)
                .paymentKey(autoPaymentResponseDto.getPaymentKey())
                .failCode(null)
                .failMessage(null)
                .build();
    }

    private void doPaymentSuccess(String userId, SubscriptionCreatedEvent subscriptionCreatedEvent, BillingDto billingDto, AutoPaymentResponseDto autoPaymentResponseDto) {
        //결제 성공시 billing Entity 수정
        dtoUpdateWhenSuccess(subscriptionCreatedEvent, billingDto, autoPaymentResponseDto);

        BillingEntity billingEntity = new ModelMapper().map(billingDto, BillingEntity.class);

        billingRepository.save(billingEntity);

        //성공 이벤트 생성
        PaymentSuccessEvent paymentSuccessEvent = new PaymentSuccessEvent(userId, subscriptionCreatedEvent.subscribeId() ,billingEntity.getNextBillingAt());
        log.info(paymentSuccessEvent.toString());

        // Kafka 이벤트 발행 (예: 결제 성공 이벤트) to subscribe service
        paymentSuccessProducer.sendPaymentSuccessEvent("payment.success", paymentSuccessEvent);

        log.info("자동결제 성공: paymentKey={}, userId={}", autoPaymentResponseDto.getPaymentKey(), userId);
    }

    private void doPaymentFail(BillingDto billingDto, AutoPaymentResponseDto autoPaymentResponseDto) {
        // 실패 시 실패 횟수 증가 및 저장
        updateFailCountWhenTossPayRequest(billingDto);
        // 엔티티 저장
        billingRepository.save(new ModelMapper().map(billingDto, BillingEntity.class));

        // Kafka 이벤트 발행 (예: 결제 실패 이벤트)
        // producer.paymentFailed(...);

        String errorStatus = autoPaymentResponseDto != null ? autoPaymentResponseDto.getStatus() : "응답 없음";
        throw new IllegalStateException("자동결제 실패: status=" + errorStatus);
    }

    private static void updateFailCountWhenTossPayRequest(BillingDto billingDto) {
        billingDto.setFailCount(
                billingDto.getFailCount() == null ? 1 : billingDto.getFailCount() + 1
        );
    }

    private static void dtoUpdateWhenSuccess(SubscriptionCreatedEvent subscriptionCreatedEvent, BillingDto billingDto, AutoPaymentResponseDto autoPaymentResponseDto) {
        billingDto.setLastPaidAt(autoPaymentResponseDto.getApprovedAt().toLocalDateTime());
        billingDto.setNextBillingAt(autoPaymentResponseDto.getApprovedAt().toLocalDateTime().plusMonths(1)); // 주기 설정 필요 시 수정
        billingDto.setAutoBillingActivated(true);
        billingDto.setFailCount(0);
        billingDto.setCustomerEmail(subscriptionCreatedEvent.userEmail());
        billingDto.setCustomerName(subscriptionCreatedEvent.userName());
        billingDto.setOrderName(subscriptionCreatedEvent.subscribeName());
        billingDto.setAmount(subscriptionCreatedEvent.subscribeAmount());
    }

    private static AutoPaymentRequestDto getAutoPaymentRequestDto(String userId, SubscriptionCreatedEvent subscriptionCreatedEvent, String orderId) {
        AutoPaymentRequestDto autoPaymentRequestDto = AutoPaymentRequestDto.builder()
                .customerKey(userId)
                .amount(subscriptionCreatedEvent.subscribeAmount())
                .orderId(orderId)
                .orderName(subscriptionCreatedEvent.subscribeName())
                .customerEmail(subscriptionCreatedEvent.userName())
                .customerName(subscriptionCreatedEvent.userEmail())
                .taxFreeAmount(0)
                .build();
        return autoPaymentRequestDto;
    }

    private BillingEntity saveBillingResponse(BillingKeyResponseDto billingResponse) {
        return BillingEntity.builder()
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
    }
}
