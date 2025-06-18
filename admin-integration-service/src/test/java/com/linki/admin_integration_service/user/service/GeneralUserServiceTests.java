package com.linki.admin_integration_service.user.service;

import com.linki.admin_integration_service.domain.user.dto.GeneralUSerDTO;
import com.linki.admin_integration_service.domain.user.dto.GeneralUserSearchRequestDTO;
import com.linki.admin_integration_service.domain.user.service.GeneralUserService;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class GeneralUserServiceTests {

    @Autowired
    private GeneralUserService generalUserService;

    @Test
    @DisplayName("일반회원 가져오기 Service Test")
    public void testGeneralUserService() {
        List<GeneralUSerDTO> generalUsers = generalUserService.getAllGeneralUsers();
        log.info("일반회원 수 : {}", generalUsers.size());
    }


    @Test
    @DisplayName("일반회원 검색 Mapper Test")
    public void searchTestGeneralUserMapper() {

        // 이름 검색
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO.setSearchType("name");
        generalUserSearchRequestDTO.setKeyword("사용자0");
        List<GeneralUSerDTO> dtoList = generalUserService.searchGeneralUser(generalUserSearchRequestDTO);
        log.info("이름검색 : {}",dtoList.size());

        //이메일 검색
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO2 = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO2.setSearchType("email");
        generalUserSearchRequestDTO2.setKeyword("user0@example.com");
        List<GeneralUSerDTO> dtoList2 = generalUserService.searchGeneralUser(generalUserSearchRequestDTO2);
        log.info("이메일검색 : {}",dtoList2.size());

        //연락처 검색
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO3 = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO3.setSearchType("phone");
        generalUserSearchRequestDTO3.setKeyword("010-5831-6607");
        List<GeneralUSerDTO> dtoList3 = generalUserService.searchGeneralUser(generalUserSearchRequestDTO3);
        log.info("phone : {}",dtoList3.size());

        //keyword가 빈경우
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO4 = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO4.setSearchType("email");
        generalUserSearchRequestDTO4.setKeyword("");
        List<GeneralUSerDTO> dtoList4 = generalUserService.searchGeneralUser(generalUserSearchRequestDTO4);
        log.info("keyword 가 빈경우 : {}",dtoList4.size());


        //SearchType 빈경우
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO5 = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO5.setSearchType("");
        generalUserSearchRequestDTO5.setKeyword("2141241");
        List<GeneralUSerDTO> dtoList5 = generalUserService.searchGeneralUser(generalUserSearchRequestDTO5);
        log.info("searchType가 빈경우 : {}",dtoList5.size());

        //keyword,SearchType 둘다 빈경우
        GeneralUserSearchRequestDTO generalUserSearchRequestDTO6 = new GeneralUserSearchRequestDTO();
        generalUserSearchRequestDTO6.setSearchType("");
        generalUserSearchRequestDTO6.setKeyword("");
        List<GeneralUSerDTO> dtoList6 = generalUserService.searchGeneralUser(generalUserSearchRequestDTO6);
        log.info("둘다 빈경우 : {}",dtoList6.size());


    }
}
