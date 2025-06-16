package com.linki.admin_integration_service.operations.repository;

import com.linki.admin_integration_service.config.MyBatisConfig;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
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
public class InfluencerReviewsMapperTests {

    @Autowired
    private InfluencerReviewsMapper influencerReviewsMapper;

    @Test
    @DisplayName("인플루언서 리뷰 Mapper Test")
    public void getInfluencerReviewsMapper() {
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsMapper.getAllInfluencerReviews();
        log.info("influencerReviewsMapper.getAllInfluencerReviews()");
        for(InfluencerReviewDTO influencerReviewDTO : influencerReviewDTOList){
            log.info(influencerReviewDTO.toString());
        }
    }


    @Test
    @DisplayName("인플루언서 리뷰 검색 테스트")
    public void searchInfluencerReviewsMapper() {

        // keyword만 빈경우
        log.info("keyword만 빈경우");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO = new InfluencerReviewSearchRequestDTO();
        influencerReviewSearchDTO.setSearchType("writer");
        List<InfluencerReviewDTO> influencerReviewDTOList = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO);
        log.info("List : {}", influencerReviewDTOList.size());

        // searchType만 빈경우 -> 전체 리스트 천개 나올경우 정상
        log.info("searchType만 빈경우");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO2 = new InfluencerReviewSearchRequestDTO();
        influencerReviewSearchDTO2.setKeyword("writer");
        List<InfluencerReviewDTO> influencerReviewDTOList2 = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO2);
        log.info("List : {}", influencerReviewDTOList2.size());
        int size = influencerReviewDTOList2.size();
        assertThat(size).isEqualTo(1000);

        
        // keyword,searchType 둘다 빈경우
        log.info("searchType만 빈경우");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO3 = new InfluencerReviewSearchRequestDTO();
        List<InfluencerReviewDTO> influencerReviewDTOList3 = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO3);
        log.info("List : {}", influencerReviewDTOList3.size());

        // searchType = 인플루언서 검색
        log.info("인플루언서 검색");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO4 = new InfluencerReviewSearchRequestDTO();
        influencerReviewSearchDTO4.setKeyword("채널1");
        influencerReviewSearchDTO4.setSearchType("influencer");
        List<InfluencerReviewDTO> influencerReviewDTOList4 = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO4);
        log.info("List : {}", influencerReviewDTOList4.size());



        // searchType = 작성자 검색
        log.info("작성자 검색");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO5 = new InfluencerReviewSearchRequestDTO();
        influencerReviewSearchDTO5.setKeyword("회사1");
        influencerReviewSearchDTO5.setSearchType("writer");
        List<InfluencerReviewDTO> influencerReviewDTOList5 = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO5);
        log.info("List : {}", influencerReviewDTOList5.size());

        // searchType = 계약 ID 검색
        log.info("계약 ID 검색");
        InfluencerReviewSearchRequestDTO influencerReviewSearchDTO6 = new InfluencerReviewSearchRequestDTO();
        influencerReviewSearchDTO6.setKeyword("CONT0001");
        influencerReviewSearchDTO6.setSearchType("contractId");
        List<InfluencerReviewDTO> influencerReviewDTOList6 = influencerReviewsMapper.searchInfluencerReviews(influencerReviewSearchDTO6);
        log.info("List : {}", influencerReviewDTOList6.size());
    }
}
