package com.Gritty.Linki.client.chatClient.service;

import com.Gritty.Linki.client.chatClient.dto.AdvertiserDto;
import com.Gritty.Linki.client.chatClient.dto.InfluencerDto;
import com.Gritty.Linki.client.chatClient.dto.PartnerInfoDto;
import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.UserRepository;
import com.Gritty.Linki.domain.user.advertiser.campaign.repository.CampaignRepository;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.entity.*;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ProposalStatus;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatClientServiceImpl implements ChatClientService {

    private final CampaignRepository campaignRepository;
    private final ProposalRepository proposalRepository;
    private final ContractRepository contractRepository;
    private final UserRepository userRepository;
    private final ChannelJpaRepository channelJpaRepository;

    private final ModelMapper modelMapper;


    /*
    * 권한별 파트너 객체 생성
    */
    @Override
    public PartnerInfoDto getPartnerByProposal(CustomUserDetails user, String proposalId){
        Object partner = getPartner(user,proposalId);

        if (partner instanceof AdvertiserDto advertiser) {
            User partnerUser = userRepository.findById(advertiser.getUserId()).orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다. "));
            return PartnerInfoDto.builder()
                    .partnerId(partnerUser.getUserId())
                    .partnerName(partnerUser.getUserLoginId())
                    .proposalId(proposalId)
                    .negoStatus(getNegoStatust(proposalId))
                    .build();
        }
        else {
            Channel channel = channelJpaRepository.findById(String.valueOf(((InfluencerDto) partner).getInfluencerId())).orElseThrow(()->new RuntimeException("존재하지않는 채널입니다."));
            User partnerUser = userRepository.findById(((InfluencerDto) partner).getUserId()).orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다. "));
            return PartnerInfoDto.builder()
                    .partnerId(partnerUser.getUserId())
                    .partnerName(partnerUser.getUserLoginId())
                    .proposalId(proposalId)
                    .profileImage(channel.getChannelBannerUrl())
                    .negoStatus(getNegoStatust(proposalId))
                    .build();
        }
    }

    /*파트너 아이디 조회
     * 1. 로그인한 유저의 롤을 확인
     * 2. 인플루언서 : 제안서 객체의 캠페인 아이디 - 캠페인의 제안서아이디와 일치하는 객체의 광고주 리턴
     * 3. 광고주 : 제안서 객체의 인플루언서 리턴
     */
    @Override
    public Object getPartner(CustomUserDetails user, String proposalId){
        String role = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElseThrow(()->new RuntimeException("권한 없음"));

        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(()->new RuntimeException("존재하지 않는 제안서"));

        if(role.equals("ROLE_INFLUENCER")){
            Campaign campaign = proposal.getCampaign();
            return modelMapper.map(campaign.getAdvertiser(), AdvertiserDto.class);
        } else {
            return modelMapper.map(proposal.getInfluencer(), InfluencerDto.class);
        }
    }

    /*
    * 계약 진행 상태 조회
    */
    public String getNegoStatust(String proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(() -> new RuntimeException("존재하지 않는 제안서 입니다. "));
        Contract contract = contractRepository.findByProposalId(proposalId).orElseThrow(() -> new RuntimeException("계약서가 존재하지 않습니다."));

        if (contract == null) return proposal.getStatus().toString();
        else return contract.getContractStatus().toString();
    }
}
