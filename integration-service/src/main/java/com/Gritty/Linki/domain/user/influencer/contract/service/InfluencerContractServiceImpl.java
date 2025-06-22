package com.Gritty.Linki.domain.user.influencer.contract.service;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.repository.jpa.ContractRepository;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.util.AuthenticationUtil;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InfluencerContractServiceImpl implements InfluencerContractService {
    private final ContractRepository contractRepository;
    private final AuthenticationUtil authenticationUtil;

    @Override
    public List<ContractListResponseDTO> getContractsByStatus(List<ContractStatus> statuses) {
        // 로그인한 인플루언서 ID 가져오기
        CustomUserDetails userDetails =
                (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String influencerId = authenticationUtil.getInfluencerIdFromUserDetails(userDetails);

        // 계약 목록 조회
        return contractRepository.findContractsByInfluencerIdAndStatus(influencerId, statuses);
    }
}
