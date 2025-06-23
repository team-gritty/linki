package com.Gritty.Linki.domain.account.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestJoinDto {
    private String userLoginId;
    private String userLoginPw;
    private String userName;
    private String userPhone;
    private String userEmail;
}
