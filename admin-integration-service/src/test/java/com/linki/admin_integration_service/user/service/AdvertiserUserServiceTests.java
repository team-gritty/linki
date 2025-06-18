package com.linki.admin_integration_service.user.service;

import com.linki.admin_integration_service.domain.user.dto.AdvertiserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.dto.AdvertiserUserDTO;
import com.linki.admin_integration_service.domain.user.service.AdvertiserUserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class AdvertiserUserServiceTests {

    @Autowired
    private AdvertiserUserService advertiserUserService;

    @Test
    @DisplayName("광고주 회원 가져오기")
    public void getAllAdvertiserUsers() {
        List<AdvertiserUserDTO> advertiserUserDTOS = advertiserUserService.getAllAdvertiserUsers();
        log.info(advertiserUserDTOS.toString());
        log.info("광고주 회원 수 : {}",advertiserUserDTOS.size());
    }

    @Test
    @DisplayName("광고주 회원 검색")
    public void searchAdvertiserUsers() {

        // keyword = null
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO.setSearchType("name");
        advertiserSearchRequestDTO.setKeyword("");
        List<AdvertiserUserDTO> advertiserUserDTOList1 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO);
        log.info("테스트 1 : {}",advertiserUserDTOList1.size());

        // searchType = null
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO2 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO2.setSearchType("");
        advertiserSearchRequestDTO2.setKeyword("홍길동");
        List<AdvertiserUserDTO> advertiserUserDTOList2 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO2);
        log.info("테스트 2 : {}",advertiserUserDTOList2.size());

        // keyword,searchType = null
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO3 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO3.setSearchType("");
        advertiserSearchRequestDTO3.setKeyword("");
        List<AdvertiserUserDTO> advertiserUserDTOList3 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO3);
        log.info("테스트 3 : {}",advertiserUserDTOList3.size());


        // searchType = advertiserId
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO4 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO4.setSearchType("advertiserId");
        advertiserSearchRequestDTO4.setKeyword("ADV0002");
        List<AdvertiserUserDTO> advertiserUserDTOList4 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO4);
        log.info("테스트 4 : {}",advertiserUserDTOList4.size());

        // searchType = name
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO5 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO5.setSearchType("name");
        advertiserSearchRequestDTO5.setKeyword("사용자971");
        List<AdvertiserUserDTO> advertiserUserDTOList5 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO5);
        log.info("테스트 5 : {}",advertiserUserDTOList5.size());


        // searchType = email
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO6 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO6.setSearchType("email");
        advertiserSearchRequestDTO6.setKeyword("user971@example.com");
        List<AdvertiserUserDTO> advertiserUserDTOList6 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO6);
        log.info("테스트 6 : {}",advertiserUserDTOList6.size());

        // searchType = phone
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO7 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO7.setSearchType("phone");
        advertiserSearchRequestDTO7.setKeyword("010-4476-5685");
        List<AdvertiserUserDTO> advertiserUserDTOList7 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO7);
        log.info("테스트 7 : {}",advertiserUserDTOList7.size());

        // searchType = businessNumber
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO8 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO8.setSearchType("businessNumber");
        advertiserSearchRequestDTO8.setKeyword("123-45-0006");
        List<AdvertiserUserDTO> advertiserUserDTOList8 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO8);
        log.info("테스트 8 : {}",advertiserUserDTOList8.size());

        // searchType = businessName
        AdvertiserSearchRequestDTO advertiserSearchRequestDTO9 = new AdvertiserSearchRequestDTO();
        advertiserSearchRequestDTO9.setSearchType("businessName");
        advertiserSearchRequestDTO9.setKeyword("회사10");
        List<AdvertiserUserDTO> advertiserUserDTOList9 = advertiserUserService.searchAdvertiserUser(advertiserSearchRequestDTO9);
        log.info("테스트 9 : {}",advertiserUserDTOList9.size());


    }
}
