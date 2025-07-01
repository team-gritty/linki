package com.linki.admin_integration_service.contract.service;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import com.linki.admin_integration_service.domain.contract.service.CampaignService;
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
public class CampaignServiceTests {

    @Autowired
    private CampaignService campaignService;

    @Test
    @DisplayName("캠페인 불러오기 Mapper Test")
    public void getAllCampaigns() {
        List<CampaignDTO> campaignDTOList = campaignService.getCampaigns();
        log.info("campaignDTOList={}", campaignDTOList.size());
    }

    @Test
    @DisplayName("캠페인 검색 Mapper Test")
    public void searchCampaigns() {
        // keyword = null, searchType = notNull
        CampaignSearchDTO dto1 = new CampaignSearchDTO();
        dto1.setSearchType("campaignId");
        dto1.setKeyword("");
        List<CampaignDTO> result1 = campaignService.searchCampaign(dto1);
        log.info("searchType=campaignId, keyword='' -> result={}", result1.size());

        // keyword = notNull, searchType = null
        CampaignSearchDTO dto2 = new CampaignSearchDTO();
        dto2.setKeyword("1"); // 캠페인ID 예시: 1
        dto2.setSearchType("");
        List<CampaignDTO> result2 = campaignService.searchCampaign(dto2);
        log.info("searchType='', keyword='1' -> result={}", result2.size());

        // keyword = null, searchType = null
        CampaignSearchDTO dto3 = new CampaignSearchDTO();
        dto3.setKeyword("");
        dto3.setSearchType("");
        List<CampaignDTO> result3 = campaignService.searchCampaign(dto3);
        log.info("searchType='', keyword='' -> result={}", result3.size());

        // searchType = campaignId
        CampaignSearchDTO dto4 = new CampaignSearchDTO();
        dto4.setSearchType("campaignId");
        dto4.setKeyword("1");
        List<CampaignDTO> result4 = campaignService.searchCampaign(dto4);
        log.info("searchType=campaignId, keyword='1' -> result={}", result4.size());

        // searchType = advertiserName
        CampaignSearchDTO dto5 = new CampaignSearchDTO();
        dto5.setSearchType("advertiserName");
        dto5.setKeyword("사용자"); // 실제 데이터에 맞게 값 세팅
        List<CampaignDTO> result5 = campaignService.searchCampaign(dto5);
        log.info("searchType=advertiserName, keyword='사용자' -> result={}", result5.size());

        // searchType = businessNumber
        CampaignSearchDTO dto6 = new CampaignSearchDTO();
        dto6.setSearchType("businessNumber");
        dto6.setKeyword("123"); // 실제 사업자번호 입력
        List<CampaignDTO> result6 = campaignService.searchCampaign(dto6);
        log.info("searchType=businessNumber, keyword='123' -> result={}", result6.size());

        // searchType = phone
        CampaignSearchDTO dto7 = new CampaignSearchDTO();
        dto7.setSearchType("phone");
        dto7.setKeyword("010-"); // 실제 전화번호 입력
        List<CampaignDTO> result7 = campaignService.searchCampaign(dto7);
        log.info("searchType=phone, keyword='010' -> result={}", result7.size());

        // searchType = campaignTitle
        CampaignSearchDTO dto8 = new CampaignSearchDTO();
        dto8.setSearchType("campaignTitle");
        dto8.setKeyword("캠페인"); // 실제 제목 입력
        List<CampaignDTO> result8 = campaignService.searchCampaign(dto8);
        log.info("searchType=campaignTitle, keyword='캠페인' -> result={}", result8.size());
    }
}
