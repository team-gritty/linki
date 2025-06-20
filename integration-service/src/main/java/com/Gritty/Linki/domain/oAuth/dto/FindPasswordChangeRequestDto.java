package com.Gritty.Linki.domain.oAuth.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordChangeRequestDto {
    private String userName;
    private String userLoginId;
    private String userEmail;
    private String verificationCode;
    private String newPassword;
} 