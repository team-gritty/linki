package com.linki.admin_integration_service.operations.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.AdvertiserReviewsMapper;
import com.linki.admin_integration_service.domain.operations.repository.myBatis.InfluencerReviewsMapper;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Log4j2
@Import(MyBatisConfig.class)
public class AdvertiserReviewsMapperTests {

    @Autowired
    private AdvertiserReviewsMapper advertiserReviewsMapper;

    @Test
    @DisplayName("광고주 리뷰 Mapper Test")
    public void getAdvertiserReviewsMapper() {
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsMapper.getAllAdvertiserReviews();
        for(AdvertiserReviewDTO advertiserReviewDTO : advertiserReviewDTOS) {
            log.info(advertiserReviewDTO.toString());
        }
    }


    @Test
    @DisplayName("광고주 리뷰 검색 테스트")
    public void searchAdvertiserReviewsMapper() {

        // keyword만 빈경우
        log.info("keyword만 빈경우");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO = new AdvertiserReviewSearchRequestDTO();
        advertiserReviewSearchRequestDTO.setKeyword("writer");
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO);
        log.info("List : {}", advertiserReviewDTOS.size());

        // searchType만 빈경우 -> 전체 리스트 천개 나올경우 정상
        log.info("searchType만 빈경우");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO2 = new AdvertiserReviewSearchRequestDTO();
        advertiserReviewSearchRequestDTO2.setSearchType("writer");
        List<AdvertiserReviewDTO> advertiserReviewDTOS2 = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO);
        log.info("List : {}", advertiserReviewDTOS2.size());


        
        // keyword,searchType 둘다 빈경우
        log.info("둘다 빈경우");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO3 = new AdvertiserReviewSearchRequestDTO();
        List<AdvertiserReviewDTO> advertiserReviewDTOS3 = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO3);
        log.info("List : {}", advertiserReviewDTOS3.size());

        // searchType = 광고주 검색
        log.info("광고주 검색");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO4 = new AdvertiserReviewSearchRequestDTO();
        advertiserReviewSearchRequestDTO4.setKeyword("회사111");
        advertiserReviewSearchRequestDTO4.setSearchType("advertiser");
        List<AdvertiserReviewDTO> advertiserReviewDTOS4 = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO4);
        log.info("List : {}", advertiserReviewDTOS4.size());



        // searchType = 작성자 검색
        log.info("작성자 검색");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO5 = new AdvertiserReviewSearchRequestDTO();
        advertiserReviewSearchRequestDTO5.setKeyword("회사1");
        advertiserReviewSearchRequestDTO5.setSearchType("writer");
        List<AdvertiserReviewDTO> advertiserReviewDTOS5 = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO5);
        log.info("List : {}", advertiserReviewDTOS5.size());

        // searchType = 계약 ID 검색
        log.info("계약 ID 검색");
        AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO6 = new AdvertiserReviewSearchRequestDTO();
        advertiserReviewSearchRequestDTO6.setKeyword("CONT0001");
        advertiserReviewSearchRequestDTO6.setSearchType("contractId");
        List<AdvertiserReviewDTO> advertiserReviewDTOS6 = advertiserReviewsMapper.searchAdvertiserReviews(advertiserReviewSearchRequestDTO6);
        log.info("List : {}", advertiserReviewDTOS6.size());
    }
}
