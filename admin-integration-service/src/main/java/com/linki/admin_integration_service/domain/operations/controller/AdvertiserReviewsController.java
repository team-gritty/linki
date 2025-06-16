package com.linki.admin_integration_service.domain.operations.controller;


import com.linki.admin_integration_service.domain.operations.dto.*;
import com.linki.admin_integration_service.domain.operations.service.AdvertiserReviewsService;
import com.linki.admin_integration_service.domain.operations.service.InfluencerReviewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdvertiserReviewsController {

    private final AdvertiserReviewsService advertiserReviewsService;
    private final ModelMapper modelMapper;


    @GetMapping("/v1/admin/api/advertisersReviews")
    public ResponseEntity<List<AdvertiserResponseDTO>> getAllAdvertiserReviews() {
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsService.getAllAdvertiserReviews();
        List<AdvertiserResponseDTO> advertiserResponseDTOS = advertiserReviewDTOS.stream()
            .map(dto -> modelMapper.map(dto, AdvertiserResponseDTO.class))
            .toList();
        return ResponseEntity.ok(advertiserResponseDTOS);
    }

    @PostMapping("/v1/admin/api/advertisersReviews/visibility")
    public ResponseEntity<Boolean> updateAdvertiserReviewVisibility(@RequestBody AdvertiserReviewVisibilityRequestDTO advertiserReviewVisibilityRequestDTO) {
        log.info(advertiserReviewVisibilityRequestDTO);
        Boolean result = advertiserReviewsService.updateAdvertiserReviewVisibility(advertiserReviewVisibilityRequestDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/v1/admin/api/advertisersReviews/search")
    public ResponseEntity<List<AdvertiserResponseDTO>> searchAdvertiserReviews(@RequestBody AdvertiserReviewSearchRequestDTO advertiserReviewSearchRequestDTO) {
        List<AdvertiserReviewDTO> advertiserReviewDTOS = advertiserReviewsService.searchAllAdvertiserReviews(advertiserReviewSearchRequestDTO);
        List<AdvertiserResponseDTO> advertiserResponseDTOList = advertiserReviewDTOS.stream()
                .map(dto -> modelMapper.map(dto, AdvertiserResponseDTO.class))
                .toList();
        return ResponseEntity.ok(advertiserResponseDTOList);
    }

}
