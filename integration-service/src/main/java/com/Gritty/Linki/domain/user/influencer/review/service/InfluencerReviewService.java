package com.Gritty.Linki.domain.user.influencer.review.service;

import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.InfAdvertiserReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReceivedInfluencerReviewResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.ReviewableContractResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.review.WrittenAdvertiserReviewResponseDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface InfluencerReviewService {

    // 로그인한 인플루언서의 정산 완료된 계약에 대해 리뷰 작성
    void submitAdvertiserReview(InfAdvertiserReviewRequestDTO request) throws AccessDeniedException;

    // 리뷰 작성 가능한 정산 완료된 계약 목록 조회
    List<ReviewableContractResponseDTO> getReviewableContracts();

    // 특정 광고주가 등록한 캠페인의 리뷰 조회
    List<InfAdvertiserReviewResponseDTO> getAdvertiseReviewsByCampaign(String campaignId);

    // 로그인한 인플루언서가 받은 리뷰 조회
    List<ReceivedInfluencerReviewResponseDTO> getReceivedInfluencerReviews();

    // 로그인한 인플루언서가 광고주에게 작성한 리뷰 조회
    List<WrittenAdvertiserReviewResponseDTO> getWrittenAdvertiserReviews();





}
