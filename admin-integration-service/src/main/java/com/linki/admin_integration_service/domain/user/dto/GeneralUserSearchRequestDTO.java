package com.linki.admin_integration_service.domain.user.dto;

import lombok.Data;

@Data
public class GeneralUserSearchRequestDTO {
    private String	searchType;
    private String	keyword;
}
