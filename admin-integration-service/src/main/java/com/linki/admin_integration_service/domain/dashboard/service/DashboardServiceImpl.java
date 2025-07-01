package com.linki.admin_integration_service.domain.dashboard.service;

import com.linki.admin_integration_service.domain.dashboard.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {


    private final TrendChartService trendChartService;
    private final ContractStatusService contractStatusService;
    private final DashboardSummaryService dashboardSummaryService;
    private final DashBoardLLMService dashboardLLM;

    @Override
    public DashboardResponseDTO dashboard() {
        DashboardResponseDTO dashboardResponseDTO = new DashboardResponseDTO();
        dashboardResponseDTO.setContractStatus(contractStatusService.getContractStatus());
        dashboardResponseDTO.setTrendChart(trendChartService.getDashboardTrendChart());
        dashboardResponseDTO.setDashboardSummary(dashboardSummaryService.getDashboardSummary());
        dashboardResponseDTO.setLLM(dashboardLLM.getDashboardLLM());
        return dashboardResponseDTO;
    }

}
