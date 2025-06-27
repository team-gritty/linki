package com.Gritty.Linki.domain.user.mypage.bizNumberCheck.DTO;

import jakarta.persistence.Entity;
import lombok.*;


@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvertiserRegisterRequest {
    private String businessNumber;
    private String companyName;
}
