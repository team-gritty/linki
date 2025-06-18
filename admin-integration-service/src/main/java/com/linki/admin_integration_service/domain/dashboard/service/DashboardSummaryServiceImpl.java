package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.DashBoardSummaryDTO;
import com.linki.admin_integration_service.domain.dashboard.dto.MonthlyRevenueDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class DashboardSummaryServiceImpl implements DashboardSummaryService {

    private final DashBoardSummaryMapper dashBoardSummaryMapper;

    @Override
    public DashBoardSummaryDTO getDashboardSummary() {
        DashBoardSummaryDTO dashboardSummaryDTO = new DashBoardSummaryDTO();
        dashboardSummaryDTO.setTotalAdvertisers(dashBoardSummaryMapper.getAdvertiserCount());
        dashboardSummaryDTO.setTotalInfluencers(dashBoardSummaryMapper.getInfluencerCount());
        dashboardSummaryDTO.setActiveCampaigns(dashBoardSummaryMapper.getActiveCampaignCount());
        dashboardSummaryDTO.setOngoingContracts(dashBoardSummaryMapper.getOngoingContracts());
        dashboardSummaryDTO.setCurrentSubscribers(dashBoardSummaryMapper.getCurrentSubscribers());
        dashboardSummaryDTO.setMonthlyRevenue(MonthlyRevenue());
        return dashboardSummaryDTO;
    }

    private int MonthlyRevenue() {
        YearMonth thisMonth = YearMonth.now();
        return dashBoardSummaryMapper.getMonthlyRevenue().stream()
                .filter(dto -> dto.getPaymentDate() != null &&
                        YearMonth.from(dto.getPaymentDate()).equals(thisMonth))
                .mapToInt(MonthlyRevenueDTO::getAmount)
                .sum();
    }

}
