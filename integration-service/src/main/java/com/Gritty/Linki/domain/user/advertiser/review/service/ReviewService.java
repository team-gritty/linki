package com.Gritty.Linki.domain.user.advertiser.review.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.review.request.ReviewWriteRequest;
import com.Gritty.Linki.domain.user.advertiser.review.response.InfluencerReviewRes;
import com.Gritty.Linki.domain.user.advertiser.review.response.ReceivedReviewResponse;
import com.Gritty.Linki.domain.user.advertiser.review.response.GivenReviewResponse;

import java.util.List;

public interface ReviewService {

    /**
     * 광고주가 받은 리뷰 조회
     * 
     * @param user 인증된 사용자 정보
     * @return 받은 리뷰 목록
     */
    List<ReceivedReviewResponse> getReceivedReviews(CustomUserDetails user);

    /**
     * 광고주가 작성한 리뷰 조회
     * 
     * @param user 인증된 사용자 정보
     * @return 작성한 리뷰 목록
     */
    List<GivenReviewResponse> getGivenReviews(CustomUserDetails user);

    /**
     * 인플루언서에 대한 리뷰 작성
     * 
     * @param user       인증된 사용자 정보
     * @param contractId 계약 ID
     * @param request    리뷰 작성 요청
     */
    void writeInfluencerReview(CustomUserDetails user, String contractId, ReviewWriteRequest request);

    /**
     * 특정 인플루언서의 리뷰 조회 (채널 상세 페이지에서 필요함)
     * 
     * @param influencerId 인플루언서 ID
     * @return 인플루언서 리뷰 목록
     */
    List<InfluencerReviewRes> getInfluencerReviews(String influencerId);
}