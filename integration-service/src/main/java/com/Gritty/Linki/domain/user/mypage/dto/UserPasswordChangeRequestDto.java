package com.Gritty.Linki.domain.user.mypage.dto;

import lombok.Data;
 
@Data
public class UserPasswordChangeRequestDto {
    private String currentPassword;
    private String newPassword;
} 