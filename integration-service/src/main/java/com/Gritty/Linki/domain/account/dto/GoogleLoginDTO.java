package com.Gritty.Linki.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleLoginDTO {
    private String sub;      // 구글 ID
    private String email;
    private String name;
}
