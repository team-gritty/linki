package com.Gritty.Linki.domain.user.influencer.contract.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.service.InfluencerContractService;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InfluencerContractController {

    private final InfluencerContractService influencerContractService;

    /**
     * 로그인한 인플루언서의 계약 목록을 상태에 따라 조회
     *
     * @param statuses 계약 상태 리스트 (e.g., ongoing, completed, etc.)
     * @return ContractListResponseDTO 리스트
     */
    @GetMapping("/v1/api/influencer/mypage/contracts")
    public List<ContractListResponseDTO> getMyContracts(
            @RequestParam("status") List<ContractStatus> statuses
    ) {

        return influencerContractService.getContractsByStatus(statuses);
    }

    @GetMapping("/v1/api/influencer/mypage/contracts/{contractId}")
    public ResponseEntity<ContractDetailResponseDTO> getContractDetail(
            @PathVariable String contractId) {

        ContractDetailResponseDTO response =
                influencerContractService.getContractDetailForInfluencer(contractId);

        return ResponseEntity.ok(response);
    }

}
