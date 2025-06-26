package com.linki.admin_integration_service.domain.user.dto;

import lombok.Data;

@Data
public class InfluencerUserResponseDTO {

    private String userId;
    private String name;
    private String email;
    private String phone;
    private String snsChannelName;
    private String snsLink;
    private String userCursor;
}
