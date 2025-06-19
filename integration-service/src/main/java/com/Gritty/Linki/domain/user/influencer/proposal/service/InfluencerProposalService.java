package com.Gritty.Linki.domain.user.influencer.proposal.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ProposalRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalListResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.ProposalResponseDTO;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface InfluencerProposalService {

    // 제안서 등록
    ProposalResponseDTO submitProposal(CustomUserDetails customUserDetails, ProposalRequestDTO proposalRequestDTO);

    // 로그인 한 인플루언서 회원의 제안서 조회
    List<ProposalListResponseDTO> getMyProposals(CustomUserDetails customUserDetails);

    //인플루언서 회원의 제안서 수정
    void updateProposal(CustomUserDetails user, String propsalId, ProposalRequestDTO proposalRequestDTO) throws AccessDeniedException;

    //인플루언서 회원의 특정 제안서 상세조회
    ProposalDetailResponseDTO getProposalDetail(CustomUserDetails user, String propsalId);

    // 인플루언서 회원의 특정 제안서 삭제
    void deleteProposal(CustomUserDetails user, String propsalId) throws AccessDeniedException;
}
