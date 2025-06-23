package com.Gritty.Linki.domain.user.influencer.settlement.service;

import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;

import java.util.List;

public interface InfluencerSettlementService {
    List<SettlementResponseDTO> getCompletedSettlementsForInfluencer();
}
