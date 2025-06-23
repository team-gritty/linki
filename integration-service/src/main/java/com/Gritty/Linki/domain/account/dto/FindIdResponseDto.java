package com.Gritty.Linki.domain.account.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindIdResponseDto {
    private String userId;
    private String userName;
    private String userEmail;
} 