package com.linki.admin_integration_service.user.service;

import com.linki.admin_integration_service.domain.user.dto.SubscriberSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.SubscriberUserDTO;
import com.linki.admin_integration_service.domain.user.service.SubscriberUserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class SubscriberUserServiceTests {

    @Autowired
    private SubscriberUserService subscriberUserService;


    @Test
    @DisplayName("구독 회원 조회 Mapper Test")
    public void getAllSubscriberUsers() {
        List<SubscriberUserDTO> subscriberUserDTOS = subscriberUserService.getAllSubscriberUsers();
        for (SubscriberUserDTO subscriberUserDTO : subscriberUserDTOS) {
            log.info(subscriberUserDTO.toString());
        }
    }


    @Test
    @DisplayName("구독 회원 검색 Mapper Test")
    public void searchSubscriberUser() {
        // 1. keyword, searchType 모두 빈 경우
        SubscriberSearchRequestDTO dto1 = new SubscriberSearchRequestDTO();
        dto1.setSearchType("");
        dto1.setKeyword("");
        List<SubscriberUserDTO> result1 = subscriberUserService.searchSubscriberUser(dto1);
        log.info("테스트 1 (모두 빈값): {}", result1.size());

        // 2. keyword만 빈 경우
        SubscriberSearchRequestDTO dto2 = new SubscriberSearchRequestDTO();
        dto2.setSearchType("userId");
        dto2.setKeyword("");
        List<SubscriberUserDTO> result2 = subscriberUserService.searchSubscriberUser(dto2);
        log.info("테스트 2 (keyword 빈값): {}", result2.size());

        // 3. searchType만 빈 경우
        SubscriberSearchRequestDTO dto3 = new SubscriberSearchRequestDTO();
        dto3.setSearchType("");
        dto3.setKeyword("user0@example.com");
        List<SubscriberUserDTO> result3 = subscriberUserService.searchSubscriberUser(dto3);
        log.info("테스트 3 (searchType 빈값): {}", result3.size());

        // 4. searchType = userId
        SubscriberSearchRequestDTO dto4 = new SubscriberSearchRequestDTO();
        dto4.setSearchType("userId");
        dto4.setKeyword("USER0000");
        List<SubscriberUserDTO> result4 = subscriberUserService.searchSubscriberUser(dto4);
        log.info("테스트 4 (userId): {}", result4.size());
        result4.forEach(dto -> log.info(dto.toString()));

        // 5. searchType = name
        SubscriberSearchRequestDTO dto5 = new SubscriberSearchRequestDTO();
        dto5.setSearchType("name");
        dto5.setKeyword("사용자0");
        List<SubscriberUserDTO> result5 = subscriberUserService.searchSubscriberUser(dto5);
        log.info("테스트 5 (name): {}", result5.size());
        result5.forEach(dto -> log.info(dto.toString()));

        // 6. searchType = email
        SubscriberSearchRequestDTO dto6 = new SubscriberSearchRequestDTO();
        dto6.setSearchType("email");
        dto6.setKeyword("user0@example.com");
        List<SubscriberUserDTO> result6 = subscriberUserService.searchSubscriberUser(dto6);
        log.info("테스트 6 (email): {}", result6.size());
        result6.forEach(dto -> log.info(dto.toString()));

        // 7. searchType = phone
        SubscriberSearchRequestDTO dto7 = new SubscriberSearchRequestDTO();
        dto7.setSearchType("phone");
        dto7.setKeyword("010-5831-6607");
        List<SubscriberUserDTO> result7 = subscriberUserService.searchSubscriberUser(dto7);
        log.info("테스트 7 (phone): {}", result7.size());
        result7.forEach(dto -> log.info(dto.toString()));
    }
}

