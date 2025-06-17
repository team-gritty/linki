package com.linki.admin_integration_service.domain.user.dto;

import lombok.Data;

@Data
public class AdvertiserResponseDTO {
    private String userId;
    private String businessNumber;
    private String businessName;
    private String name;
    private String phone;
    private String email;
}
