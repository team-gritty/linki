package com.ssg.chatservice.config.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//json 파싱 맵핑 dto
public class LoginRequestDto {
    private String userLoginId;
    private String userLoginPw;
}
