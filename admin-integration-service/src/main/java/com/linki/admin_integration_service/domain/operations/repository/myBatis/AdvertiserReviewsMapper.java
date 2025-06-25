package com.linki.admin_integration_service.domain.operations.repository.myBatis;

import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewDTO;
import com.linki.admin_integration_service.domain.operations.dto.AdvertiserReviewSearchRequestDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdvertiserReviewsMapper {
    List<AdvertiserReviewDTO> getAllAdvertiserReviews();
    List<AdvertiserReviewDTO> searchAdvertiserReviews(AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO);


    List<AdvertiserReviewDTO> getAllAdvertiserReviewsWithKeyset(@Param("cursor") String cursor, @Param("size") int size);
    List<AdvertiserReviewDTO> searchAdvertiserReviewsWithKeyset(@Param("searchDTO") AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO, @Param("cursor") String cursor, @Param("size") int size);
    
    // 리뷰 상세 정보 별도 조회
    List<Map<String, Object>> getReviewDetailsByContractIds(@Param("contractIds") List<String> contractIds);
}

