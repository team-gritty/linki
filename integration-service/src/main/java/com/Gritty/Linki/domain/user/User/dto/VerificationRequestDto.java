package com.Gritty.Linki.domain.user.User.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequestDto {
    private String userName;
    private String userEmail;
    private String verificationCode;
} 