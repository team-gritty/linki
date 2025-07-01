package com.linki.admin_integration_service.domain.contract.service;

import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignKeysetResponseDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;

import java.util.List;

public interface CampaignService {
    List<CampaignDTO> getCampaigns();
    List<CampaignDTO> searchCampaign(CampaignSearchDTO campaignSearchDTO);
    String exportExcel();
    
    CampaignKeysetResponseDTO getCampaignsWithKeyset(String cursor, int size);
    CampaignKeysetResponseDTO searchCampaignWithKeyset(CampaignSearchDTO campaignSearchDTO, String cursor, int size);
}
