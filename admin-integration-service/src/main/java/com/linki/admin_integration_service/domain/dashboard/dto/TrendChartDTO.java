package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrendChartDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Map<String, Integer>> newMembers; // 신규 회원 수
    private Map<String, Map<String, Integer>> newAdvertisers; // 신규 광고주 전환 수
    private Map<String, Map<String, Integer>> newInfluencers; // 신규 인플루언서 전환 수
}
