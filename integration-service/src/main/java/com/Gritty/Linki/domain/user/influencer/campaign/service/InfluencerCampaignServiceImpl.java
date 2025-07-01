package com.Gritty.Linki.domain.user.influencer.campaign.service;

import com.Gritty.Linki.config.security.CustomUserDetails;

import com.Gritty.Linki.domain.user.influencer.campaign.repository.jpa.InfluencerCampaignRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignCategoryResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.campaign.CampaignListResponseDTO;
import com.Gritty.Linki.entity.Campaign;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.Category;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Log4j2
public class InfluencerCampaignServiceImpl implements InfluencerCampaignService {

    private final InfluencerCampaignRepository campaignRepository;
    private final ModelMapper modelMapper;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public List<CampaignListResponseDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(campaign -> modelMapper.map(campaign, CampaignListResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<CampaignListResponseDTO> getAllCampaignsWithPagination(Pageable pageable) {
        Page<Campaign> campaignPage = campaignRepository.findAll(pageable);
        log.info(pageable);
        return campaignPage.map(this::toListDto);
    }

    @Override
    public CampaignDetailResponseDTO getCampaignDetailById(String campaignId) {
        // Optional에서 값 꺼내기 + 예외 처리
      Campaign campaign = campaignRepository.findByCampaignIdWithAdvertiser(campaignId)
              .orElseThrow(() -> new EntityNotFoundException("해당 캠페인을 찾을 수 없습니다."));

      // ModelMapper로 DTO 매핑
        CampaignDetailResponseDTO responseDTO = modelMapper.map(campaign, CampaignDetailResponseDTO.class);

        // advertiser -> companyName 수동세팅
        responseDTO.setCompanyName(campaign.getAdvertiser().getCompanyName());

        return responseDTO;

    }

    @Override
    public List<CampaignListResponseDTO> getCampaignsByCategory(Category category) {
       
        
        // 1) repository 호출 분기
        List<Campaign> entities = (category == null)
                ? campaignRepository.findAll()
                : campaignRepository.findAllByCampaignCategory(category);

      

        // 2) DTO 변환
        return entities.stream()
                .map(this::toListDto)
                .collect(Collectors.toList());

    }

    @Override
    public Page<CampaignListResponseDTO> getCampaignsByCategoryWithPagination(Category category, Pageable pageable) {
        Page<Campaign> campaignPage = (category == null)
                ? campaignRepository.findAll(pageable)
                : campaignRepository.findAllByCampaignCategory(category, pageable);
        
        return campaignPage.map(this::toListDto);
    }


    /**
     * 인플루언서 본인의 proposalId 에 연관된 캠페인만 조회
     */
    @Override
    public CampaignDetailResponseDTO getCampaignByProposalId(String proposalId) {
        // 1) 로그인된 인플루언서 userId (customUserDetails)로 influencerId 조회
        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()
        );

        // 2) proposalId + influencerId 로 Campaign + Advertiser fetch-join 조회
        Campaign campaign = campaignRepository
                .findCampaignByProposalIdAndInfluencerId(proposalId, influencerId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "proposalId=" + proposalId +
                                " 는 influencerId=" + influencerId + " 의 소유가 아닙니다."
                ));

        // 3) DTO 매핑
        CampaignDetailResponseDTO dto = modelMapper.map(campaign, CampaignDetailResponseDTO.class);
        dto.setCompanyName(campaign.getAdvertiser().getCompanyName());
        return dto;
    }

    @Override
    public List<CampaignCategoryResponseDTO> getCategories() {
        return Arrays.stream(Category.values())
                .map(category -> CampaignCategoryResponseDTO.builder()
                        .name(category.name())           // "BEAUTY"
                        .displayName(category.getDisplayName()) // "뷰티"
                        .build())
                .collect(Collectors.toList());
    }


    /**
     * Entity → CampaignListResponseDTO 변환 헬퍼(타입이 달라서 사용)
     */
    private CampaignListResponseDTO toListDto(Campaign campaign) {
        // 1) 자동 매핑 가능한 필드
        CampaignListResponseDTO dto = modelMapper.map(campaign, CampaignListResponseDTO.class);

        // 2) 수동 매핑 (STRICT 모드 고려)
        // - LocalDateTime → LocalDate
        dto.setCreatedAt(campaign.getCreatedAt().toLocalDate().atStartOfDay());
        dto.setCampaignDeadline(campaign.getCampaignDeadline().toLocalDate().atStartOfDay());
        // - enum → enum/String
        dto.setCampaignCategory(campaign.getCampaignCategory());
        dto.setCampaignPublishStatus(campaign.getCampaignPublishStatus());

        return dto;
    }


}
