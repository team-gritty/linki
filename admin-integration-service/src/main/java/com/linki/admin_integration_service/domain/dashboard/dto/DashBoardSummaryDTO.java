package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;
import java.io.Serializable;

@Data
public class DashBoardSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int	totalAdvertisers; // 전체 광고주 수
    private int	totalInfluencers; // 전체 인플루언서 수
    private int	activeCampaigns; // 전체 진행중인 캠페인 수
    private int	ongoingContracts; // 진행중 계약 수
    private int	currentSubscribers; // 현재 구독수
    private long	MonthlyRevenue; // 이번달 매출
}
