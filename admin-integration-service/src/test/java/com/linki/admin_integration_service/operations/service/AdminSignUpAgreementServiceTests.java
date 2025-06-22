package com.linki.admin_integration_service.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.service.AdminSignUpAgreementService;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class AdminSignUpAgreementServiceTests {

    @Autowired
    private AdminSignUpAgreementService adminSignUpAgreementService;

    @Test
    @DisplayName("가입대기 관리자 리스트 가져오기 Service Test")
    public void getAdminSignUpAgreementService() {
        List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOList
                = adminSignUpAgreementService.getAdminSignUpList();
        log.info("가입대기 관리자 리스트 가져오기");
        log.info("가입대기 관리자 수 : {}", adminSignUpAgreementDTOList.size());
    }

    @Test
    @DisplayName("가입대기 관리자 검색 Service Test")
    public void searchAdminSignUpAgreementService() {
        // keyword만 빈 경우
        log.info("keyword만 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto1 = new AdminSignUpAgreementSearchRequestDTO();
        dto1.setSearchType("adminEmail");
        List<AdminSignUpAgreementDTO> result1 = adminSignUpAgreementService.searchAdminSignUp(dto1);
        log.info("List : {}", result1.size());

        // searchType만 빈 경우
        log.info("searchType만 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto2 = new AdminSignUpAgreementSearchRequestDTO();
        dto2.setKeyword("admin24@linki.com");
        if (dto2.getSearchType() == null) {
            dto2.setSearchType("");
        }
        List<AdminSignUpAgreementDTO> result2 = adminSignUpAgreementService.searchAdminSignUp(dto2);
        log.info("List : {}", result2.size());

        // 둘 다 빈 경우
        log.info("searchType, keyword 둘 다 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto3 = new AdminSignUpAgreementSearchRequestDTO();
        if (dto3.getSearchType() == null) {
            dto3.setSearchType("");
        }
        List<AdminSignUpAgreementDTO> result3 = adminSignUpAgreementService.searchAdminSignUp(dto3);
        log.info("List : {}", result3.size());

        // adminName 검색
        log.info("adminName 검색");
        AdminSignUpAgreementSearchRequestDTO dto4 = new AdminSignUpAgreementSearchRequestDTO();
        dto4.setSearchType("adminName");
        dto4.setKeyword("관리자24");
        List<AdminSignUpAgreementDTO> result4 = adminSignUpAgreementService.searchAdminSignUp(dto4);
        log.info("List : {}", result4.size());

        // adminEmail 검색
        log.info("adminEmail 검색");
        AdminSignUpAgreementSearchRequestDTO dto5 = new AdminSignUpAgreementSearchRequestDTO();
        dto5.setSearchType("adminEmail");
        dto5.setKeyword("admin24@linki.com");
        List<AdminSignUpAgreementDTO> result5 = adminSignUpAgreementService.searchAdminSignUp(dto5);
        log.info("List : {}", result5.size());

        // adminPhone 검색
        log.info("adminPhone 검색");
        AdminSignUpAgreementSearchRequestDTO dto6 = new AdminSignUpAgreementSearchRequestDTO();
        dto6.setSearchType("adminPhone");
        dto6.setKeyword("010-6348-7752");
        List<AdminSignUpAgreementDTO> result6 = adminSignUpAgreementService.searchAdminSignUp(dto6);
        log.info("List : {}", result6.size());
    }

    @Test
    @Transactional
    @DisplayName("관리자 가입 승인 Service Test")
    public void approveAdminSignUpAgreementService() {
        AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO = new AdminSignUpAgreementRequestDTO();
        adminSignUpAgreementRequestDTO.setAdminSignUpId("admin38");
        adminSignUpAgreementService.approveAdminSignUp(adminSignUpAgreementRequestDTO);
    }

    @Test
    @Transactional
    @DisplayName("관리자 가입 거절 Service Test")
    public void rejectAdminSignUpAgreementService() {
        AdminSignUpAgreementRequestDTO adminSignUpAgreementRequestDTO = new AdminSignUpAgreementRequestDTO();
        adminSignUpAgreementRequestDTO.setAdminSignUpId("admin38");
        adminSignUpAgreementService.rejectAdminSignUp(adminSignUpAgreementRequestDTO);
    }
}
