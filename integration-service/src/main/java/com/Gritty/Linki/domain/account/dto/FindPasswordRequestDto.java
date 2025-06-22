package com.Gritty.Linki.domain.account.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FindPasswordRequestDto {
    private String userName;
    private String userLoginId;
    private String userEmail;
} 