package com.Gritty.Linki.domain.user.influencer.settlement.controller;

import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;
import com.Gritty.Linki.domain.user.influencer.settlement.service.InfluencerSettlementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class InfluencerSettlementController {

    private final InfluencerSettlementService influencerSettlementService;

    @GetMapping("/v1/api/influencer/mypage/settlements")
    public ResponseEntity<List<SettlementResponseDTO>>getSettlementsLists(){
        List<SettlementResponseDTO> settlements = influencerSettlementService.getCompletedSettlementsForInfluencer();
        return ResponseEntity.ok(settlements);

    }
}
