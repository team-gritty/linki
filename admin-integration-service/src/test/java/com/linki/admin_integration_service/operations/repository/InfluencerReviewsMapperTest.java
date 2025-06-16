package com.linki.admin_integration_service.operations.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.repository.InfluencerReviewsMapper;
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
public class InfluencerReviewsMapperTest {

    @Autowired
    private InfluencerReviewsMapper influencerReviewsMapper;

    @Test
    @DisplayName("인플루언서 리뷰 Mapper Test")
    public void mapperTest(){
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsMapper.getAllInfluencerReviews();
        log.info("influencerReviewsMapper.getAllInfluencerReviews()");
        for(InfluencerReviewDTO influencerReviewDTO : influencerReviewDTOList){
            log.info(influencerReviewDTO.toString());
        }
    }
}
