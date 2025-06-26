package com.linki.admin_integration_service.domain.user.dto;

import lombok.Data;

@Data
public class SubscriberSearchRequestDTO {
    private String	searchType;
    private String	keyword;
    private String cursor;
    private Integer size;
}
