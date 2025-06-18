package com.linki.admin_integration_service.domain.contract.repository.myBatis;

import com.linki.admin_integration_service.domain.contract.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.contract.dto.CampaignSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CampaignMapper {
    List<CampaignDTO> getCampaigns();
    List<CampaignDTO> searchCampaign(CampaignSearchDTO campaignSearchDTO);


}
