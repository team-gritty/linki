package com.linki.admin_integration_service.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsServiceImpl;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;


@SpringBootTest
@Log4j2
public class InfluencerReviewsServiceTests {

    @Autowired
    private InfluencerReviewsServiceImpl influencerReviewsService;

    @Test
    @DisplayName("인플루언서 리뷰 가져오기 Service Test")
    public void  testService() {
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsService.getAllInfluencerReviews();
        for(InfluencerReviewDTO influencerReviewDTO : influencerReviewDTOList) {
            log.info(influencerReviewDTO);
        }
    }


    @Test
    @DisplayName("인플루언서 리뷰 검색 Service Test")
    public void searchInfluencerReviews() {
        // keyword만 빈경우
        log.info("keyword만 빈경우");
        InfluencerReviewSearchRequestDTO dto1 = new InfluencerReviewSearchRequestDTO();
        dto1.setSearchType("writer");
        List<InfluencerReviewDTO> result1 = influencerReviewsService.searchAllInfluencerReviews(dto1);
        log.info("List : {}", result1.size());

        // searchType만 빈경우
        log.info("searchType만 빈경우");
        InfluencerReviewSearchRequestDTO dto2 = new InfluencerReviewSearchRequestDTO();
        dto2.setKeyword("writer");
        List<InfluencerReviewDTO> result2 = influencerReviewsService.searchAllInfluencerReviews(dto2);
        log.info("List : {}", result2.size());


        // keyword, searchType 둘 다 빈경우
        log.info("둘 다 빈경우");
        InfluencerReviewSearchRequestDTO dto3 = new InfluencerReviewSearchRequestDTO();
        List<InfluencerReviewDTO> result3 = influencerReviewsService.searchAllInfluencerReviews(dto3);
        log.info("List : {}", result3.size());

        // searchType = 인플루언서 검색
        log.info("인플루언서 검색");
        InfluencerReviewSearchRequestDTO dto4 = new InfluencerReviewSearchRequestDTO();
        dto4.setKeyword("채널1");
        dto4.setSearchType("influencer");
        List<InfluencerReviewDTO> result4 = influencerReviewsService.searchAllInfluencerReviews(dto4);
        log.info("List : {}", result4.size());

        // searchType = 작성자 검색
        log.info("작성자 검색");
        InfluencerReviewSearchRequestDTO dto5 = new InfluencerReviewSearchRequestDTO();
        dto5.setKeyword("회사1");
        dto5.setSearchType("writer");
        List<InfluencerReviewDTO> result5 = influencerReviewsService.searchAllInfluencerReviews(dto5);
        log.info("List : {}", result5.size());

        // searchType = 계약 ID 검색
        log.info("계약 ID 검색");
        InfluencerReviewSearchRequestDTO dto6 = new InfluencerReviewSearchRequestDTO();
        dto6.setKeyword("CONT0001");
        dto6.setSearchType("contractId");
        List<InfluencerReviewDTO> result6 = influencerReviewsService.searchAllInfluencerReviews(dto6);
        log.info("List : {}", result6.size());
    }

}
