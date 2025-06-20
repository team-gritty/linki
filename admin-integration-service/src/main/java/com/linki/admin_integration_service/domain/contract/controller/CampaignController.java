package com.linki.admin_integration_service.domain.contract.controller;

import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import com.linki.admin_integration_service.domain.contract.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;
    private final ModelMapper modelMapper;

    @GetMapping("/v1/admin/api/campaigns")
    public ResponseEntity<List<CampaignResponseDTO>> getCampaigns() {
        List<CampaignDTO> campaignDTOS = campaignService.getCampaigns();
        List<CampaignResponseDTO> campaignResponseDTOList =
                campaignDTOS.stream()
                        .map(dto -> modelMapper.map(dto, CampaignResponseDTO.class))
                        .toList();

        return ResponseEntity.ok(campaignResponseDTOList);
    }

    @PostMapping("/v1/admin/api/campaigns/search")
    public ResponseEntity<List<CampaignResponseDTO>> searchCampaign(@RequestBody CampaignSearchDTO campaignSearchDTO) {
        List<CampaignDTO> campaignDTOS = campaignService.searchCampaign(campaignSearchDTO);
        List<CampaignResponseDTO> campaignResponseDTOList =
                campaignDTOS.stream()
                        .map(dto -> modelMapper.map(dto, CampaignResponseDTO.class))
                        .toList();
        return ResponseEntity.ok(campaignResponseDTOList);

    }

    @PostMapping("/v1/admin/api/campaigns/exportExcel")
    public ResponseEntity<String> exportExcel(){
        return ResponseEntity.ok(campaignService.exportExcel());
    }
}
