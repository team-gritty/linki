package com.linki.admin_integration_service.payment.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.payment.dto.SettlementDTO;
import com.linki.admin_integration_service.domain.payment.dto.SettlementSearchDTO;
import com.linki.admin_integration_service.domain.payment.repository.myBatis.SettlementMapper;
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
public class SettlementMapperTests {

    @Autowired
    private SettlementMapper settlementMapper;

    @Test
    @DisplayName("전체 정산 정보 가져오기 Mapper Test")
    public void getAllSettlements() {
        List<SettlementDTO> dtoList = settlementMapper.getAllSettlements();
        log.info(" 테스트 결과 : {}", dtoList.size());
    }

    @Test
    @DisplayName("정산 정보 검색 Mapper Test")
    public void searchSettlements() {
        // keyword = null / searchType = notNull
        SettlementSearchDTO dto = new SettlementSearchDTO();
        dto.setKeyword("");
        dto.setSearchType("");
        List<SettlementDTO> dtoList = settlementMapper.searchSettlement(dto);
        log.info(" keyword, searchType 빈 문자열 검색 결과 : {}", dtoList.size());

        // keyword = notNull / searchType = null
        SettlementSearchDTO dto2 = new SettlementSearchDTO();
        dto2.setKeyword("사용자");
        dto2.setSearchType(null);
        List<SettlementDTO> dtoList2 = settlementMapper.searchSettlement(dto2);
        log.info(" keyword만 있는 검색 결과 : {}", dtoList2.size());

        // keyword = null / searchType = null
        SettlementSearchDTO dto3 = new SettlementSearchDTO();
        dto3.setKeyword(null);
        dto3.setSearchType(null);
        List<SettlementDTO> dtoList3 = settlementMapper.searchSettlement(dto3);
        log.info(" 검색조건 없는 결과 : {}", dtoList3.size());

        // searchType = contractId
        SettlementSearchDTO dto4 = new SettlementSearchDTO();
        dto4.setKeyword("CONT0003");
        dto4.setSearchType("contractId");
        List<SettlementDTO> dtoList4 = settlementMapper.searchSettlement(dto4);
        log.info(" 계약 ID 검색 결과 : {}", dtoList4.size());

        // searchType = advertiserName
        SettlementSearchDTO dto5 = new SettlementSearchDTO();
        dto5.setKeyword("사용자500");
        dto5.setSearchType("advertiserName");
        List<SettlementDTO> dtoList5 = settlementMapper.searchSettlement(dto5);
        log.info(" 광고주명 검색 결과 : {}", dtoList5.size());

        // searchType = influencerName
        SettlementSearchDTO dto6 = new SettlementSearchDTO();
        dto6.setKeyword("사용자1");
        dto6.setSearchType("influencerName");
        List<SettlementDTO> dtoList6 = settlementMapper.searchSettlement(dto6);
        log.info(" 인플루언서명 검색 결과 : {}", dtoList6.size());

    }


}
