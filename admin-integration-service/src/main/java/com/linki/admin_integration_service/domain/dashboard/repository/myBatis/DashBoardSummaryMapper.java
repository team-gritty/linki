package com.linki.admin_integration_service.domain.dashboard.repository.myBatis;

import com.linki.admin_integration_service.domain.dashboard.dto.CampaignDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.MonthlyRevenueDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.TrendMapperDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DashBoardSummaryMapper {
    int getAdvertiserCount();
    int getInfluencerCount();
    int getActiveCampaignCount();
    int getOngoingContracts();
    int getCurrentSubscribers();
    List<MonthlyRevenueDTO> getMonthlyRevenue();
    List<TrendMapperDTO> getEnterDay();
    List<CampaignDTO> getCampaigns();
}
