package com.linki.admin_integration_service.payment.service;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeDTO;
import com.linki.admin_integration_service.domain.payment.dto.PaymentSubscribeSearchDTO;
import com.linki.admin_integration_service.domain.payment.service.PaymentSubscribeService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Import(MyBatisConfig.class)
@Log4j2
public class PaymentSubscribeServiceTest {

    @Autowired
    private PaymentSubscribeService paymentSubscribeService;

    @Test
    @DisplayName("구독 결제 관리 가져오기 Service Test")
    public void getAllPaymentSubscribesTest(){
        List<PaymentSubscribeDTO> paymentSubscribeDTOS = paymentSubscribeService.getAllPaymentSubscribes();
        log.info("All PaymentSubscribes: {}", paymentSubscribeDTOS);
    }

    @Test
    @DisplayName("구독 결제 관리 검색 Service Test")
    public void searchPaymentSubscribesTest() {
        // searchType = null / keyword = notNull
        PaymentSubscribeSearchDTO dto1 = new PaymentSubscribeSearchDTO();
        dto1.setSearchType("");
        dto1.setKeyword("테스트"); // 적절한 값으로
        List<PaymentSubscribeDTO> result1 = paymentSubscribeService.searchPaymentSubscribe(dto1);
        log.info("searchType=null, keyword=notNull: {}", result1.size());

        // searchType = notNull / keyword = null
        PaymentSubscribeSearchDTO dto2 = new PaymentSubscribeSearchDTO();
        dto2.setSearchType("name"); // 예시로 name
        dto2.setKeyword(""); // null이나 빈값
        List<PaymentSubscribeDTO> result2 = paymentSubscribeService.searchPaymentSubscribe(dto2);
        log.info("searchType=notNull, keyword=null: {}", result2.size());

        // searchType = null / keyword = null
        PaymentSubscribeSearchDTO dto3 = new PaymentSubscribeSearchDTO();
        dto3.setSearchType("");
        dto3.setKeyword("");
        List<PaymentSubscribeDTO> result3 = paymentSubscribeService.searchPaymentSubscribe(dto3);
        log.info("searchType=null, keyword=null: {}", result3.size());

        // searchType = name / keyword = notNull
        PaymentSubscribeSearchDTO dto4 = new PaymentSubscribeSearchDTO();
        dto4.setSearchType("name");
        dto4.setKeyword("홍길동");
        List<PaymentSubscribeDTO> result4 = paymentSubscribeService.searchPaymentSubscribe(dto4);
        log.info("searchType=name, keyword=홍길동: {}", result4.size());

        // searchType = loginId / keyword = notNull
        PaymentSubscribeSearchDTO dto5 = new PaymentSubscribeSearchDTO();
        dto5.setSearchType("loginId");
        dto5.setKeyword("user");
        List<PaymentSubscribeDTO> result5 = paymentSubscribeService.searchPaymentSubscribe(dto5);
        log.info("searchType=loginId, keyword=user: {}", result5.size());

        // searchType = phone / keyword = notNull
        PaymentSubscribeSearchDTO dto6 = new PaymentSubscribeSearchDTO();
        dto6.setSearchType("phone");
        dto6.setKeyword("010");
        List<PaymentSubscribeDTO> result6 = paymentSubscribeService.searchPaymentSubscribe(dto6);
        log.info("searchType=phone, keyword=010: {}", result6.size());
    }
}
