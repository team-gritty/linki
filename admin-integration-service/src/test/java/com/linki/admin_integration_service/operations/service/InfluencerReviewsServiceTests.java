package com.linki.admin_integration_service.operations.service;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
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
}
