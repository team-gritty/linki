package com.linki.admin_integration_service.domain.contract.dto;

import com.linki.admin_integration_service.common.BaseSearchDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CampaignSearchDTO extends BaseSearchDTO {
    // Keyset 페이지네이션을 위한 필드들
    private String cursor;
    private Integer size;
}
