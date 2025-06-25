package com.Gritty.Linki.domain.user.influencer.contract.controller;

import com.Gritty.Linki.config.security.CustomUserDetails;
import com.Gritty.Linki.domain.user.influencer.contract.service.AdvertiserContractService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractDetailResponseDTO;
import com.Gritty.Linki.domain.user.influencer.responseDTO.contract.ContractListResponseDTO;
import com.Gritty.Linki.vo.enums.ContractStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdvertiserContractController {

    private final AdvertiserContractService advertiserContractService;


    /**
     * 로그인한 인플루언서의 계약 목록을 상태에 따라 조회
     *
     * @param statuses 계약 상태 리스트 (e.g., ongoing, completed, etc.)
     * @return ContractListResponseDTO 리스트
     */
    @GetMapping("/v1/api/advertiser/mypage/contracts")
    public List<ContractListResponseDTO> getMyContracts(
            @RequestParam("status") List<ContractStatus> statuses
    ) {

        return advertiserContractService.getContractsByStatus(statuses);
    }

    @GetMapping("/v1/api/advertiser/mypage/contracts/{contractId}")
    public ResponseEntity<ContractDetailResponseDTO> getContractDetail(
            @PathVariable String contractId) {

        ContractDetailResponseDTO response =
                advertiserContractService.getContractDetail(contractId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/v1/api/advertiser/mypage/contracts/{proposalId}/create")
    public ResponseEntity<ContractCreateRequestDTO> createContract(@PathVariable String proposalId, @RequestBody ContractCreateRequestDTO dto,
                                                                   @AuthenticationPrincipal CustomUserDetails user) {
        advertiserContractService.CreateContract(dto,user);

        log.info(dto.toString());
        log.info("amount : " + dto.getContractAmount());
        log.info("시작일 :  " +dto.getContractStartDate());
        log.info("proposal id " + proposalId);

        return ResponseEntity.ok(dto);



    }

}
