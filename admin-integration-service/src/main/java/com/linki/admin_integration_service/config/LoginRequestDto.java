package com.linki.admin_integration_service.config;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//json 파싱 맵핑 dto
public class LoginRequestDto {
    private String adminLoginId;
    private String adminLoginPw;
}
