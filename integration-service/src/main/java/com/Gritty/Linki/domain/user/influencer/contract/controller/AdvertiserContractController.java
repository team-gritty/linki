package com.Gritty.Linki.domain.user.influencer.contract.controller;

import com.Gritty.Linki.domain.user.influencer.contract.service.AdvertiserContractService;
import com.Gritty.Linki.domain.user.influencer.requestDTO.ContractCreateRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdvertiserContractController {

    private final AdvertiserContractService advertiserContractService;

    @PostMapping("v1/api/advertiser/mypage/contract/{proposalId}/create")
    public ResponseEntity<Void> createAdvertiserContract(@PathVariable int proposalId, ContractCreateRequestDTO dto) {
        log.info("proposal id " + proposalId);
        log.info("dto dto " + dto);

     return ResponseEntity.ok().build();
    }
}
