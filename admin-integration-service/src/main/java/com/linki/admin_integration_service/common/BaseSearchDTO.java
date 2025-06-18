package com.linki.admin_integration_service.common;

import lombok.Data;

@Data
public class BaseSearchDTO {
    private String searchType;
    private String keyword;
}
