package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.DashBoardSummaryDTO;
import com.linki.admin_integration_service.domain.dashboard.repository.myBatis.DashBoardSummaryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CachePut;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Log4j2
public class DashboardSummaryServiceImpl implements DashboardSummaryService {

    private final DashBoardSummaryMapper dashBoardSummaryMapper;

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @CachePut(value = "dashboard", key = "'summary'")
    public DashBoardSummaryDTO getDashboardSummary() {
        DashBoardSummaryDTO dashboardSummaryDTO = new DashBoardSummaryDTO();
        dashboardSummaryDTO.setTotalAdvertisers(dashBoardSummaryMapper.getAdvertiserCount());
        dashboardSummaryDTO.setTotalInfluencers(dashBoardSummaryMapper.getInfluencerCount());
        dashboardSummaryDTO.setActiveCampaigns(dashBoardSummaryMapper.getActiveCampaignCount());
        dashboardSummaryDTO.setOngoingContracts(dashBoardSummaryMapper.getOngoingContracts());
        dashboardSummaryDTO.setCurrentSubscribers(dashBoardSummaryMapper.getCurrentSubscribers());
        dashboardSummaryDTO.setMonthlyRevenue(MonthlyRevenue());
        log.info("📊 Calculated MonthlyRevenue = {}", MonthlyRevenue());
        return dashboardSummaryDTO;
    }

    private long MonthlyRevenue() {
        YearMonth thisMonth = YearMonth.now();
        long result = dashBoardSummaryMapper.getMonthlyRevenue().stream()
                .filter(dto -> dto.getPaymentDate() != null &&
                        YearMonth.from(dto.getPaymentDate()).equals(thisMonth))
                .peek(amount -> log.info("📌 MonthlyRevenue 개별 금액: {}", amount))
                .mapToLong(dto -> Math.max(dto.getAmount(), 0))
                .sum();
        log.info("Monthly revenue for this month is {}", result);
        return result;
    }

}
