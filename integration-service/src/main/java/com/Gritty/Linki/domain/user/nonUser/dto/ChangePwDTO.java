package com.Gritty.Linki.domain.user.nonUser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePwDTO {
    private String userLoginPw;
    private String changeLoginPw;

}
