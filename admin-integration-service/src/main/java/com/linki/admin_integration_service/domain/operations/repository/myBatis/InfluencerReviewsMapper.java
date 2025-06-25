package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface InfluencerReviewsMapper {
    List<InfluencerReviewDTO> getAllInfluencerReviews();
    List<InfluencerReviewDTO> searchInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO);


    List<InfluencerReviewDTO> getAllInfluencerReviewsWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<InfluencerReviewDTO> searchInfluencerReviewsWithKeyset(@Param("searchDTO") InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO, @Param("cursor") String cursor, @Param("size") int size);
    
    // 인플루언서 리뷰 상세 정보 별도 조회
    List<Map<String, Object>> getInfluencerReviewDetailsByContractIds(@Param("contractIds") List<String> contractIds);
}


