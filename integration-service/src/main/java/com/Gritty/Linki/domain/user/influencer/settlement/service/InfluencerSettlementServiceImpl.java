package com.Gritty.Linki.domain.user.influencer.settlement.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.responseDTO.settlement.SettlementResponseDTO;
import com.Gritty.Linki.domain.user.influencer.settlement.repository.jpa.InfSettlementRepository;
import com.Gritty.Linki.util.AuthenticationUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor


public class InfluencerSettlementServiceImpl implements InfluencerSettlementService {

    private final InfSettlementRepository infSettlementRepository;
    private final AuthenticationUtil authenticationUtil;
    @Override
    public List<SettlementResponseDTO> getCompletedSettlementsForInfluencer() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);
        return infSettlementRepository.findAllByInfluencerIdAndCompletedContract(influencerId);
    }
}
