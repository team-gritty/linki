package com.Gritty.Linki.domain.oAuth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
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
