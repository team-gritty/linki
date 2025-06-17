package com.linki.admin_integration_service.operations.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdminSignUpAgreementSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdminSignUpAgreementMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

@SpringBootTest
@Log4j2
@Import(MyBatisConfig.class)
public class AdminSignupAgreementMapperTests {

    @Autowired
    private AdminSignUpAgreementMapper adminSignUpAgreementMapper;

    @Test
    @DisplayName("관리자 가입목록 가져오기 Mapper Test")
    public void getAdminSignupAgreementMapperTests() {
        List<AdminSignUpAgreementDTO> adminSignUpAgreementDTOS = adminSignUpAgreementMapper.getAdminSignUpList();
        log.info("Admin Signup Agreement Mapper Tests Successfully");
        for(AdminSignUpAgreementDTO adminSignUpAgreementDTO : adminSignUpAgreementDTOS) {
            log.info("adminSignUpAgreementDTO: " + adminSignUpAgreementDTO);
        }
    }

    @Test
    @DisplayName("관리자 가입목록 검색 Mapper Test")
    public void searchAdminSignupAgreementDTOTests() {

        // keyword만 빈 경우
        log.info("keyword만 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto1 = new AdminSignUpAgreementSearchRequestDTO();
        dto1.setSearchType("adminEmail");
        List<AdminSignUpAgreementDTO> result1 = adminSignUpAgreementMapper.searchAdminSignUp(dto1);
        log.info("List : {}", result1.size());

        // searchType만 빈 경우
        log.info("searchType만 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto2 = new AdminSignUpAgreementSearchRequestDTO();
        dto2.setKeyword("admin24@linki.com");
        if (dto2.getSearchType() == null) {
            dto2.setSearchType("");
        }
        List<AdminSignUpAgreementDTO> result2 = adminSignUpAgreementMapper.searchAdminSignUp(dto2);
        log.info("List : {}", result2.size());

        // 둘 다 빈 경우
        log.info("searchType, keyword 둘 다 빈 경우");
        AdminSignUpAgreementSearchRequestDTO dto3 = new AdminSignUpAgreementSearchRequestDTO();
        if (dto3.getSearchType() == null) {
            dto3.setSearchType("");
        }
        List<AdminSignUpAgreementDTO> result3 = adminSignUpAgreementMapper.searchAdminSignUp(dto3);
        log.info("List : {}", result3.size());

        // adminName 검색
        log.info("adminName 검색");
        AdminSignUpAgreementSearchRequestDTO dto4 = new AdminSignUpAgreementSearchRequestDTO();
        dto4.setSearchType("adminName");
        dto4.setKeyword("관리자24");
        List<AdminSignUpAgreementDTO> result4 = adminSignUpAgreementMapper.searchAdminSignUp(dto4);
        log.info("List : {}", result4.size());

        // adminEmail 검색
        log.info("adminEmail 검색");
        AdminSignUpAgreementSearchRequestDTO dto5 = new AdminSignUpAgreementSearchRequestDTO();
        dto5.setSearchType("adminEmail");
        dto5.setKeyword("admin24@linki.com");
        List<AdminSignUpAgreementDTO> result5 = adminSignUpAgreementMapper.searchAdminSignUp(dto5);
        log.info("List : {}", result5.size());

        // adminPhone 검색
        log.info("adminPhone 검색");
        AdminSignUpAgreementSearchRequestDTO dto6 = new AdminSignUpAgreementSearchRequestDTO();
        dto6.setSearchType("adminPhone");
        dto6.setKeyword("010-6348-7752");
        List<AdminSignUpAgreementDTO> result6 = adminSignUpAgreementMapper.searchAdminSignUp(dto6);
        log.info("List : {}", result6.size());


    }
}