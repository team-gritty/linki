package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.InfluencerReviewSearchRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InfluencerReviewsMapper {
    List<InfluencerReviewDTO> getAllInfluencerReviews();
    List<InfluencerReviewDTO> searchInfluencerReviews(InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO);


    List<InfluencerReviewDTO> getAllInfluencerReviewsWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<InfluencerReviewDTO> searchInfluencerReviewsWithKeyset(@Param("searchDTO") InfluencerReviewSearchRequestDTO influencerReviewSearchRequestDTO, @Param("cursor") String cursor, @Param("size") int size);
}


