package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.Data;

@Data
public class ContractStatusDTO {
    private int	pending; 	// 광고 진행 이전 건수
    private int	active;	// 광고 진행 중 건수
    private int	completed;	// 정산 완료 건수
}
