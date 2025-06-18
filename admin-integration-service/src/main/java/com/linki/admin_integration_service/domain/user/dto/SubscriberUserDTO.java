package com.linki.admin_integration_service.domain.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SubscriberUserDTO {
    private String userId;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;
}
