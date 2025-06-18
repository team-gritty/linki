package com.Gritty.Linki.domain.user.influencer.proposal.service;

import com.Gritty.Linki.config.actuator.InfoContributor;
import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerUtilRepository;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.entity.Influencer;
import com.Gritty.Linki.entity.Proposal;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.util.IdGenerator;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InfluencerProposalServiceImpl implements InfluencerProposalService {
    private final ProposalRepository proposalRepository;
    private final AuthenticationUtil authenticationUtil;
    private final InfluencerUtilRepository influencerUtilRepository;
    private final InfluencerCampaignRepository influencerCampaignRepository;
    private final IdGenerator idGenerator;

    @Override
    public ProposalResponseDTO submitProposal(CustomUserDetails customUserDetails, ProposalRequestDTO proposalRequestDTO) {
        //1. 로그인한 인플루언서 Id 조회
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );

        Influencer influencer = influencerUtilRepository.findById(influencerId)
                .orElseThrow(()->new EntityNotFoundException("인플루언서를 찾을 수 없습니다"));



       // 2. 새로운 proposal Id 생성
        String newProposalId = idGenerator.proposalId();

        // 3. 엔티티 생성 및 저장
        Proposal proposal = Proposal.builder()
                .proposalId(newProposalId)
                .influencer(influencer)
                .campaign(Campaign.builder().campaignId(proposalRequestDTO.getCampaignId()).build())
                .contents(proposalRequestDTO.getContents())
                .submittedAt(LocalDateTime.now())
                .status(ProposalStatus.PENDING)
                .build();

        proposalRepository.save(proposal);

        // 4. ResponseDTO로 변환해서 반환
        return ProposalResponseDTO.builder()
                .proposalId(proposal.getProposalId())
                .campaignId(proposal.getCampaign().getCampaignId())
                .influencerId(proposal.getInfluencer().getInfluencerId())
                .contents(proposal.getContents())
                .submittedAt(proposal.getSubmittedAt())
                .build();



    }
}
