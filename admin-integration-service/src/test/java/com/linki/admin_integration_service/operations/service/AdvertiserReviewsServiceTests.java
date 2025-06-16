package com.linki.admin_integration_service.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.service.AdvertiserReviewsService;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@Log4j2
public class AdvertiserReviewsServiceTests {

    @Autowired
    private AdvertiserReviewsService advertiserReviewsService;

    @Test
    @DisplayName("광고주 리뷰 가져오기 Service Test")
    public void  testService() {
        List<AdvertiserReviewDTO> advertiserReviewDTOList = advertiserReviewsService.getAllAdvertiserReviews();
        for(AdvertiserReviewDTO advertiserReviewDTO : advertiserReviewDTOList) {
            log.info(advertiserReviewDTO);
        }
    }


    @Test
    @DisplayName("광고주 리뷰 검색 Service Test")
    public void searchAdvertiserReviews() {
        // keyword만 빈경우
        log.info("keyword만 빈경우");
        AdvertiserReviewSearchRequestDTO dto1 = new AdvertiserReviewSearchRequestDTO();
        dto1.setSearchType("writer");
        List<AdvertiserReviewDTO> result1 = advertiserReviewsService.searchAllAdvertiserReviews(dto1);
        log.info("List : {}", result1.size());

        // searchType만 빈경우
        log.info("searchType만 빈경우");
        AdvertiserReviewSearchRequestDTO dto2 = new AdvertiserReviewSearchRequestDTO();
        dto2.setKeyword("writer");
        List<AdvertiserReviewDTO> result2 = advertiserReviewsService.searchAllAdvertiserReviews(dto2);
        log.info("List : {}", result2.size());


        // keyword, searchType 둘 다 빈경우
        log.info("둘 다 빈경우");
        AdvertiserReviewSearchRequestDTO dto3 = new AdvertiserReviewSearchRequestDTO();
        List<AdvertiserReviewDTO> result3 = advertiserReviewsService.searchAllAdvertiserReviews(dto3);
        log.info("List : {}", result3.size());

        // searchType = 광고주 검색
        log.info("광고주 검색");
        AdvertiserReviewSearchRequestDTO dto4 = new AdvertiserReviewSearchRequestDTO();
        dto4.setKeyword("회사111");
        dto4.setSearchType("influencer");
        List<AdvertiserReviewDTO> result4 = advertiserReviewsService.searchAllAdvertiserReviews(dto4);
        log.info("List : {}", result4.size());

        // searchType = 작성자 검색
        log.info("작성자 검색");
        AdvertiserReviewSearchRequestDTO dto5 = new AdvertiserReviewSearchRequestDTO();
        dto5.setKeyword("채널241");
        dto5.setSearchType("writer");
        List<AdvertiserReviewDTO> result5 = advertiserReviewsService.searchAllAdvertiserReviews(dto5);
        log.info("List : {}", result5.size());

        // searchType = 계약 ID 검색
        log.info("계약 ID 검색");
        AdvertiserReviewSearchRequestDTO dto6 = new AdvertiserReviewSearchRequestDTO();
        dto6.setKeyword("CONT0001");
        dto6.setSearchType("contractId");
        List<AdvertiserReviewDTO> result6 = advertiserReviewsService.searchAllAdvertiserReviews(dto6);
        log.info("List : {}", result6.size());
    }

}
