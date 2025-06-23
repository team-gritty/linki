package com.Gritty.Linki.domain.user.influencer.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayUserDto {
    private String userId;
    private String userLoginId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private Integer userPayStatus;
    private Integer userStatus;
    private LocalDate userEnterDay;
    private String userRole;
}
