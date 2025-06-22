package com.Gritty.Linki.domain.account.dto;

import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinDTO {
    private String userId;
    private String userLoginId;
    private String userLoginPw;
    private String userName;
    private String userPhone;
    private String userEmail;
    private int userStatus;
    private Date userEnterDay;
    private String userRole;
}
