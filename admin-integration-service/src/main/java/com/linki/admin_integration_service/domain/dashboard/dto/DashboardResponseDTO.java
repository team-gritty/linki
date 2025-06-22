package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;

@Data
public class DashboardResponseDTO {

    private DashBoardSummaryDTO DashboardSummary;
    private TrendChartDTO trendChart;
    private ContractStatusDTO contractStatus;
    private LLMDTO LLM;
}
