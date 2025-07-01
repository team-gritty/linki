package com.linki.admin_integration_service.common;

import lombok.Data;

@Data
public class BaseSearchDTO {
    private String searchType;
    private String keyword;
    // Keyset 페이지네이션을 위한 필드들
    private String cursor;
    private Integer size;
}
