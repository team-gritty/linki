package com.linki.admin_integration_service.domain.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractStatusDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int	pending; 	// 광고 진행 이전 건수
    private int	active;	// 광고 진행 중 건수
    private int	completed;	// 정산 완료 건수
}
