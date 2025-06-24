package com.Gritty.Linki.domain.user.mypage.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserMypageResponseDto {
    private String userId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private LocalDate userEnterDay;
} 