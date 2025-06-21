package com.Gritty.Linki.domain.user.influencer.review.controller;

import com.Gritty.Linki.domain.user.influencer.requestDTO.InfAdvertiserReviewRequestDTO;
import com.Gritty.Linki.domain.user.influencer.review.service.InfluencerReviewService;
import com.Gritty.Linki.entity.Advertiser;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.nio.file.AccessDeniedException;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerReviewController  {
    private final InfluencerReviewService influencerReviewService;

    @PostMapping("/v1/api/influencer/mypage/reviews/advertiser-write")
    public ResponseEntity<String> submitAdvertiserReview(@Valid @RequestBody InfAdvertiserReviewRequestDTO requestDTO) throws AccessDeniedException {
        influencerReviewService.submitAdvertiserReview(requestDTO);
        return ResponseEntity.ok("리뷰가 성공적으로 등록되었습니다");
    }
}
