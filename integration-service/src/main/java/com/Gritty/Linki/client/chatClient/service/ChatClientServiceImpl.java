package com.Gritty.Linki.client.chatClient.service;

import com.Gritty.Linki.client.chatClient.dto.*;
import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.config.security.UserRepository;
import com.Gritty.Linki.domain.user.advertiser.campaign.repository.CampaignRepository;
import com.Gritty.Linki.domain.user.advertiser.channel.repository.jpa.ChannelJpaRepository;
import com.Gritty.Linki.domain.user.advertiser.proposal.repository.ProposalRepository;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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
            User partnerUser = userRepository.findById(((InfluencerDto) partner).getUserId()).orElseThrow(()->new RuntimeException("존재하지 않는 유저입니다. "));
            return PartnerInfoDto.builder()
                    .partnerId(partnerUser.getUserId())
                    .partnerName(partnerUser.getUserLoginId())
                    .proposalId(proposalId)
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
    @Override
    public String getNegoStatust(String proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(() -> new RuntimeException("존재하지 않는 제안서 입니다. "));
        return contractRepository.findByProposal_ProposalId(proposalId)
                .map(contract -> contract.getContractStatus().toString()) // 계약 있으면 계약 상태 반환
                .orElse(proposal.getStatus().toString());
    }

    /*
    * 캠페인 아이디로 chatInfo 객체 리스트 조회
    * */
    @Override
    public List<ChatInfoDto> getCampaignToChatInfo(String campaignId){
        List<ChatInfoDto> chatInfoDtos = new ArrayList<>();

        List<InterfaceChatInfoDto> findChatInfos = proposalRepository.findInfluencerChatInfoByCampaignId(campaignId);

        for(InterfaceChatInfoDto InterfaceChatInfoDto : findChatInfos){
            ChatInfoDto chatInfoDto = ChatInfoDto.builder()
                    .opponentId(InterfaceChatInfoDto.getUserId())
                    .opponentName(InterfaceChatInfoDto.getUserLoginId())
                    .proposalId(InterfaceChatInfoDto.getProposalId())
                    .build();
            chatInfoDtos.add(chatInfoDto);
        }
        return chatInfoDtos;
    }

    //로그인한 유저의 정보 조회
    @Override
    public List<ChatInfoDto> getUserToChatInfo(CustomUserDetails user) {
        String role = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).findFirst().orElseThrow(() -> new RuntimeException("권한 없음"));
        if(role.equals("ROLE_INFLUENCER")) {
            List<InterfaceChatInfoDto> interfaceChatInfoDtos = proposalRepository.findInfluencerChatInfoByUserId(user.getUserId());
            return getChatInfoDtos(interfaceChatInfoDtos);
        } else {
            List<InterfaceChatInfoDto> interfaceChatInfoDtos = proposalRepository.findAdvertiserChatInfoByUserId(user.getUserId());
            return getChatInfoDtos(interfaceChatInfoDtos);
        }
    }

    @Override
    //채팅정보 빌더
    public List<ChatInfoDto> getChatInfoDtos(List<InterfaceChatInfoDto> interfaceChatInfoDtos ){
        return interfaceChatInfoDtos.stream().map(interfaceChatInfoDto ->
            ChatInfoDto.builder()
                    .opponentId(interfaceChatInfoDto.getUserId())
                    .opponentName(interfaceChatInfoDto.getUserLoginId())
                    .proposalId(interfaceChatInfoDto.getProposalId())
                    .campaignId(interfaceChatInfoDto.getCampaignId())
                    .build()
        ).collect(Collectors.toList());
    }


}
